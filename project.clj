(defproject simulated-genetics "0.1.0-SNAPSHOT"
  :description "A lightweight simulation of genetics, for use in games only."
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License,version 2.0 or (at your option) any later version"
          :url "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :dependencies [[cnuernber/libpython-clj "1.33"]
                 [com.taoensso/telemere "1.0.0-beta3"] ;; Peter Taoussanis' new replacement for Timbre
                 [org.clojure/clojure "1.11.1"]
                 [org.jmonkeyengine/jme3-core "3.6.1-stable"]
  [cnuernber/libpython-clj "1.36"]]
  :main ^:skip-aot cc.journeyman.simulated-genetics.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
