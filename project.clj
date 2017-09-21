(defproject com.rentpath/rp-util-clj "1.0.11"
  :description "Common utilities"
  :url "https://github.com/rentpath/rp-util-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:url "git@github.com:rentpath/rp-util-clj.git"}
  :deploy-repositories [["releases" {:url "https://clojars.org/repo/"
                                     :username [:gpg :env/CLOJARS_USERNAME]
                                     :password [:gpg :env/CLOJARS_PASSWORD]
                                     :sign-releases false}]]
  :global-vars {*warn-on-reflection* true}
  :dependencies [[org.clojure/tools.reader "1.0.5"]
                 [org.clojure/test.check "0.9.0"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0-alpha19"]]}})
