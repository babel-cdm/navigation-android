Navigation Android
=======
Esta librería creada y mantenida por el **CDM** permite el manejo sencillo de navegación dentro de [Activities](http://developer.android.com/intl/es/guide/components/activities.html) con [Fragments](http://developer.android.com/intl/es/guide/components/fragments.html). 

Los métodos principales de los que consta 

 - `public void navigateDown(Fragment fragment, boolean addToBackStack)`
   *Añade un nuevo Fragment a la pila y lo muestra*
   
 - `public void navigateToSection(Fragment fragment, boolean
   addToBackStack)` *Añade un nuevo Fragment a la pila y borrar todos

 - `public void navigateUp()` *Borra el último fragment añadido a la
   pila*