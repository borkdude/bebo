(ns dbb.main
  (:require
   [clojure.string :as str]
   [goog.object :as gobject]
   [sci.async :as scia]
   [sci.core :as sci]
   [shadow.esm :as esm]))

(defn async-load-fn
  [{:keys [libname opts ctx ns]}]
  (let [[libname suffix] (str/split libname "$")]
       (-> (esm/dynamic-import libname)
           (.then
            (fn [js-lib]
              (let [js-lib (if suffix
                             (gobject/getValueByKeys js-lib (.split suffix "."))
                             js-lib)
                    munged (symbol (munge libname))]
                ;; register class globally in context
                (sci/add-class! ctx munged js-lib)
                (let [{:keys [as refer]} opts]
                  (when as
                    ;; import class in current namespace with reference to globally
                    ;; registed class
                    (sci/add-import! ctx ns munged as))
                  (when refer
                    (doseq [sym refer]
                      (let [prop (gobject/get js-lib sym)
                            sub-libname (str munged "$" prop)]
                        ;; register sub-library globally
                        (sci/add-class! ctx sub-libname prop)
                        ;; add import to sub-library in current namespace
                        (sci/add-import! ctx ns sub-libname sym))))))
              {:handled true})))))

(def ctx (sci/init {:async-load-fn async-load-fn
                    ;; async require override
                    :namespaces {'clojure.core {'require scia/require}}
                    ;; allow JS interop globally
                    :classes {'js goog/global :allow :all}}))

;; allow printing
(sci/alter-var-root sci/print-fn (constantly *print-fn*))

(defn init []
  (let [[file & _] js/Deno.args]
    (-> (js/Deno.readTextFile file)
        (.then (fn [text]
                 (scia/eval-string* ctx text))))))
