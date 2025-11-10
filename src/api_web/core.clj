(ns api-web.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route])
  (:gen-class))

;; Handler que responde ao GET /hello
(defn funcao-hello [request]
  {:status 200
   :body (str "Olá, "
              (get-in request [:query-params :name] "visitante")
              "! Seja bem-vindo à API Pedestal!")})

;; Definição das rotas
(def routes
  (route/expand-routes
    #{["/hello" :get funcao-hello :route-name :hello]}))

;; Mapa de configuração do serviço
(def service-map
  {::http/routes routes
   ::http/type :jetty
   ::http/port 9999
   ::http/join? false})

;; Função principal
(defn -main [& _]
  (println "🚀 Servidor iniciado em http://localhost:9999 ...")
  (-> service-map
      http/create-server
      http/start))

