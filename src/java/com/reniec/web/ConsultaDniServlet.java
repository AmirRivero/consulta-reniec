package com.reniec.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import javax.net.ssl.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;

@WebServlet("/ConsultaDniServlet")
public class ConsultaDniServlet extends HttpServlet {

   
    private void disableCertificateValidation() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        String dni = request.getParameter("dni");
        String token = "apis-token-14301.4gWKNL6IKNwOfmjaDZQF4f5fHA9uEDAE";
        String apiUrl = "https://api.apis.net.pe/v2/reniec/dni?numero=" + dni;

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 🔧 Desactiva validación SSL (solo para pruebas locales)
            disableCertificateValidation();

            URL url = new URL(apiUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            int code = conn.getResponseCode();

            if (code == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder apiResponse = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    apiResponse.append(inputLine);
                }
                in.close();

                // Procesa JSON
                JSONObject json = new JSONObject(apiResponse.toString());
                String nombres = json.getString("nombres");
                String apellidoPaterno = json.getString("apellidoPaterno");
                String apellidoMaterno = json.getString("apellidoMaterno");
                String digitoVerificador = json.getString("digitoVerificador");

                // Muestra resultado en tabla
                out.println("<html><head><title>Resultado</title></head><body>");
                out.println("<h2>Resultado de la Consulta</h2>");
                out.println("<table border='1' cellpadding='5'>");
                out.println("<tr><th>DNI</th><td>" + dni + "</td></tr>");
                out.println("<tr><th>Apellido Paterno</th><td>" + apellidoPaterno + "</td></tr>");
                out.println("<tr><th>Apellido Materno</th><td>" + apellidoMaterno + "</td></tr>");
                out.println("<tr><th>Nombres</th><td>" + nombres + "</td></tr>");
                out.println("<tr><th>Dígito Verificador</th><td>" + digitoVerificador + "</td></tr>");
                out.println("</table>");
                out.println("</body></html>");

            } else {
                out.println("<html><body><p>Error: Código HTTP " + code + "</p></body></html>");
            }

        } catch (Exception e) {
            out.println("<html><body><p>Error al consultar el API: " + e.getMessage() + "</p></body></html>");
        }
    }
}
