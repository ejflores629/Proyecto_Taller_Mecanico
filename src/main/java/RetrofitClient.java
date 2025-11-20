import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {

            // 1. Configurar el Logging Interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // NIVEL BODY: Muestra headers + contenido del JSON (Request y Response)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Configurar el Cliente OkHttp
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging) // Agregamos el log
                    .connectTimeout(30, TimeUnit.SECONDS) // Timeout de conexión
                    .readTimeout(30, TimeUnit.SECONDS)    // Timeout de lectura
                    .writeTimeout(30, TimeUnit.SECONDS)   // Timeout de escritura
                    .build();

            // 3. Construir Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
