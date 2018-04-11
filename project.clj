(defproject com.rentpath/rp-util-clj "1.0.14-SNAPSHOT"
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
  :dependencies [[org.clojure/tools.reader "1.1.1"]
                 [org.clojure/test.check "0.9.0"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0"]
                                  [circleci/circleci.test "0.4.1"]
                                  [cloverage "1.0.10"]]
                   :aliases {"test-coverage" ["run"
                                              "-m" "cloverage.coverage"
                                              "--runner" "circleci.test"
                                              "--ns-regex" "rp\\.util\\..*"
                                              "--ns-exclude-regex" "clojure\\..*"
                                              "--src-ns-path" "src"
                                              "--test-ns-path" "test"
                                              "--fail-threshold" "95"
                                              "--low-watermark" "96"
                                              "--high-watermark" "99"]}}})
