(ns api-web.servidor
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test])
  (:gen-class))

;; ---------------------------------------------------------
;; HANDLER — função que responde ao endpoint /hello
;; ---------------------------------------------------------
(defn funcao-hello [request]
  {:status 200
   :body (str "Olá, "
              (get-in request [:query-params :name] "visitante")
              "! Seja bem-vindo à API Pedestal!")})

;; ---------------------------------------------------------
;; ROTAS — definem os endpoints da aplicação
;; ---------------------------------------------------------
(def routes
  (route/expand-routes
    #{["/hello" :get funcao-hello :route-name :hello]}))

;; ---------------------------------------------------------
;; SERVICE MAP — configuração básica do servidor
;; ---------------------------------------------------------
(def service-map
  {::http/routes routes
   ::http/type :jetty
   ::http/port 9999
   ::http/join? false})

;; ---------------------------------------------------------
;; TESTE LOCAL (SEM ABRIR SERVIDOR)
;; ---------------------------------------------------------
;; Este trecho cria o serviço na memória e permite simular requests
;; sem precisar abrir uma porta nem subir o Jetty.
(def server
  (atom (http/create-server service-map)))

;; Simula uma requisição GET à rota /hello
(defn testar-hello []
  (test/response-for (::http/service-fn @server)
                     :get "/hello?name=Vitor"))

;; ---------------------------------------------------------
;; FUNÇÃO MAIN (caso queira rodar de verdade)
;; ---------------------------------------------------------
(defn -main [& _]
  (println "🚀 Servidor iniciado em http://localhost:9999 ...")
  (-> service-map
      http/create-server
      http/start))
