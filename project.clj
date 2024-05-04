(defproject simulated-genetics "0.1.0-SNAPSHOT"
  :cloverage {:output "docs/cloverage"
              :codecov? true
              :emma-xml? true}
  :codox {:froboz.cloverage {:output "docs/cloverage"
                             :codecov? true
                             :html? true
                             :debug? true}
          :metadata {:doc "**TODO**: write docs"
                     :doc/format :markdown}
          :output-path "docs/codox"
          :source-uri "https://github.com/simon-brooke/the-great-game/blob/master/{filepath}#L{line}"}
  :dependencies [[clj-python/libpython-clj "2.025"]
                 [com.taoensso/telemere "1.0.0-beta3"] ;; Peter Taoussanis' new replacement for Timbre
                 [jme-clj "0.1.13"]
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.cli "1.1.230"]
                 [org.jmonkeyengine/jme3-core "3.6.1-stable"]]
  :description "A lightweight simulation of genetics, intended for use in games only."
  :license {:name "GNU General Public License,version 2.0 or (at your option) any later version"
            :url "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :main ^:skip-aot cc.journeyman.simulated-genetics.launcher
  :plugins [[lein-cloverage "1.2.2"]
            [lein-codox "0.10.8"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :url "http://example.com/FIXME")
