# PARCIALT1-AREM
  
# Taller de verificación de conocimientos técnicos

1. clone el repositorio
    ```
   git clone https://github.com/juliandtrianar/PARCIALT1-AREM.git
   ```
2. ingrese a la ruta del proyecto
   ```
   cd PARCIALT1-AREM
   ```

3. Compile el proyecto

    ```
   mvn compile
    ```
    
4. corremos los servicios simultaneamente por separado
    ```
   java -cp target/classes edu.eci.arep.BackendService
    ```
    ```
   java -cp target/classes edu.eci.arep.FacadeService
    ```
6. Una vez el servidor diga Listo para recibir...

7. probamos su funcionamiento con

    ```
   http://localhost:8080/setkv?key=usuario&value=Julian
    ```
   <img width="639" height="252" alt="image" src="https://github.com/user-attachments/assets/855f8f74-3f00-4482-85ab-be1b58e7d0cc" />

 
   
    ```
   http://localhost:8080/getkv?key=usuario
    ```
  <img width="776" height="175" alt="image" src="https://github.com/user-attachments/assets/1f2f012e-b7d5-4621-80ce-710795f50eab" />

    ```
   http://localhost:8080/getkv?key=desconocido
    ```
   <img width="553" height="183" alt="image" src="https://github.com/user-attachments/assets/a9fb564c-fb90-41c9-a1bd-d0913863fbb5" />
   
     ```
      http://localhost:8080/setkv?key=
     ```
<img width="616" height="223" alt="image" src="https://github.com/user-attachments/assets/f4d60702-11d2-4628-8244-57f8b7f15cd6" />
   

   

