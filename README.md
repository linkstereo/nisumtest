NISUM PROJECT

La aplicacion esta hecha con Spring 6.x, Spring Security y Java 17.
Usa la base de datos embebida H2 como tambien las herramientas mas recientes para el armado de aplicaciones REST, los
endpoints hechos son:

- POST http://localhost:8080/apiv1/jwt/register
    * Permite registrar un usuario. El password es codificado antes de ser
    guardado en la base de datos.
    * Valida el email usando el Validator de Apache.
- POST http://localhost:8080/apiv1/jwt/authenticate
    * Permite autenticar un usuario. Retorna un token para poder obtener
    info de la base de datos.
- GET http://localhost:8080/apiv1/users (Necesita credenciales JWT de USER)
    * Permite obtener info de los usuarios almacenados en la base de datos
- PUT http://localhost:8080/apiv1/users (Necesita credenciales JWT de USER)
    * Permite actualizar la info de un usuario almacenado en la base de datos
    
Hay test unitarios y tambien test de integracion que validan la logica de la aplicacion
Los test de integracion realizan el camino feliz contemplando la capa de seguridad,
como tambien un escenario de error en el que la autenticacion falla.


EJECUCION DE LA APLICACION

Requerimientos de la maquina:
- Sede debe tener instalado Java 17 y Maven 3.5.x
- En la maquina local, no debe haber ninguna aplicacion usando el puerto 8080
  ya que la aplicacion nisumapp levanta en este mismo puerto.
  
Comando de ejecucion:
- mvn spring-boot:run