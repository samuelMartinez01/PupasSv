package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

/**
 * Clase que define los nombres de encabezados HTTP utilizados en la API REST.
 */
public class Headers {

    /**
     * Encabezado personalizado para indicar el número total de registros en una respuesta paginada.
     * Se usa en respuestas HTTP para informar al cliente cuántos registros hay en total.
     */
    public static final String TOTAL_RECORD = "Total-records";

    /**
     * Encabezado personalizado para indicar que la solicitud contiene datos no procesables.
     * Se usa generalmente con el código de estado HTTP 422 (Unprocessable Entity).
     */
    public static final String UNPROCESSABLE_ENTITY = "Unprocessable Entity";

    /**
     * Encabezado personalizado para indicar que uno o más parámetros en la solicitud son incorrectos.
     * Se puede usar con el código HTTP 400 (Bad Request) o 422 (Unprocessable Entity).
     */
    public static final String WRONG_PARAMETER = "Wrong-parameter";

    /**
     * Encabezado personalizado que indica que un recurso solicitado por ID no fue encontrado.
     * Se usa con el código HTTP 404 (Not Found).
     */
    public static final String NOT_FOUND_ID = "not-found-id";

    /**
     * Encabezado personalizado que indica que ocurrió un error en el procesamiento de la solicitud.
     * Se usa con el código HTTP 500 (Internal Server Error).
     */
    public static final String PROCESS_ERROR = "process-error";

    /**
     * Encabezado estándar HTTP para la autenticación.
     * Se utiliza para enviar tokens o credenciales en solicitudes protegidas.
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * Encabezado estándar HTTP que indica la ubicación de un recurso creado.
     * Se usa en respuestas con código HTTP 201 (Created) para devolver la URL del nuevo recurso.
     */
    public static final String LOCATION = "Location";

    /**
     * Encabezado estándar HTTP para identificar una solicitud única.
     * Se usa en sistemas distribuidos para rastrear solicitudes en registros y depuración.
     */
    public static final String X_REQUEST_ID = "X-Request-ID";

    /**
     * Encabezado estándar HTTP para indicar el idioma preferido del cliente.
     * Permite a la API devolver contenido localizado en el idioma solicitado.
     */
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    /**
     * Encabezado estándar HTTP para controlar el almacenamiento en caché de la respuesta.
     * Permite definir políticas como `no-cache`, `private`, `public`, o tiempos de expiración.
     */
    public static final String CACHE_CONTROL = "Cache-Control";


    public static final String X_DELETED_ID = "X-Deleted-Id";
}
