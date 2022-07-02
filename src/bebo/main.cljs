(ns bebo.main
  (:require
   [babashka.cli :as cli]
   [bebo.core :refer [run-script]]
   [clojure.string :as str]))

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
