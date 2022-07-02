(ns bebo.main
  (:require
   [clojure.string :as str]
   [goog.object :as gobject]
   [sci.async :as scia]
   [sci.core :as sci]
   [shadow.esm :as esm]
   [babashka.cli :as cli]))

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

(defn run-script [file]
  (-> (if (str/starts-with? file "http")
        (-> (js/fetch file)
            (.then #(.text %)))
        (js/Deno.readTextFile file))
      (.then (fn [text]
               (scia/eval-string* ctx text)))))

(defn run-script* [{:keys [rest-cmds args]}]
  (run-script (or (first rest-cmds) (first args))))

(defn print-help []
  (println (str/trim "Usage: bebo <subcommand> <opts>

Evaluation:
  run <file | url>  Runs a .cljs file from the filesystem or https location.

Help:
  --help / help  Print this help
")))

(defn fallback [{:keys [cmds opts]}]
  (if (:help opts)
    (print-help)
    (if-let [file (first cmds)]
      (run-script file)
      (print-help))))

(defn init []
  (let [args js/Deno.args]
    (cli/dispatch [{:cmds ["run"] :fn run-script*}
                   {:cmds [] :fn fallback}] args
                  {:aliases {:h :help}})))
