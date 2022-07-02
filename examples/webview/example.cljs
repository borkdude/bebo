(ns example
  (:require
   ["https://deno.land/x/webview/mod.ts" :refer [Webview]]
   [clojure.string :as str]))

(def template
  "<html>
  <body>
    <h1>Hello from deno v{{version}}</h1>
  </body>
  </html>")

;; we need goog.string/format here ...
(def html (str/replace template
                       "{{version}}"
                       js/Deno.version.deno))

(def webview (new Webview))

(.navigate webview (str "data:text/html," (js/encodeURIComponent html)))

(.run webview)
