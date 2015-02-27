package spa.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import com.eclipsesource.json.JsonArray;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class RestController
{

    private static String                       uri                 = "http://localhost:9000/";

    private static ResponseHandler<Set<String>> responseHandlerList = new ResponseHandler<Set<String>>() {

                                                                        @Override
                                                                        public Set<String> handleResponse(final HttpResponse response) throws IOException
                                                                        {
                                                                            StatusLine statusLine = response.getStatusLine();
                                                                            HttpEntity entity = response.getEntity();
                                                                            if(statusLine.getStatusCode() >= 300) { throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase()); }
                                                                            if(entity == null) { throw new ClientProtocolException("Response contains no content"); }
                                                                            ContentType contentType = ContentType.getOrDefault(entity);
                                                                            Charset charset = contentType.getCharset();
                                                                            Reader reader = new InputStreamReader(entity.getContent(), charset);

                                                                            JsonArray jsonArray = JsonArray.readFrom(reader);
                                                                            Set<String> ids = new HashSet<String>();
                                                                            for(int i = 0; i < jsonArray.size(); i++) {
                                                                                ids.add(jsonArray.get(i).asString());
                                                                            }

                                                                            return ids;
                                                                        }
                                                                    };

    private static ResponseHandler<Model>       responseHandlerRDF  = new ResponseHandler<Model>() {

                                                                        @Override
                                                                        public Model handleResponse(final HttpResponse response) throws IOException
                                                                        {
                                                                            StatusLine statusLine = response.getStatusLine();
                                                                            HttpEntity entity = response.getEntity();
                                                                            if(statusLine.getStatusCode() >= 300) { throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase()); }
                                                                            if(entity == null) { throw new ClientProtocolException("Response contains no content"); }

                                                                            // String m = IOUtils.toString(entity.getContent(), "UTF-8");
                                                                            // InputStream stream = new ByteArrayInputStream(m.getBytes(StandardCharsets.UTF_8));

                                                                            Model model = ModelFactory.createDefaultModel();
                                                                            model.read(entity.getContent(), null, "TURTLE");

                                                                            return model;
                                                                        }
                                                                    };


    public static String uploadModel(String operation, Model model, String method) throws Exception
    {
        CloseableHttpClient client = HttpClients.createDefault();

        HttpEntityEnclosingRequestBase request = null;

        switch(method) {
            case "POST":
                request = new HttpPost(uri + operation);
            break;
            case "PUT":
                request = new HttpPut(uri + operation);
            default:
            break;
        }

        OutputStream baos = new ByteArrayOutputStream();
        RDFDataMgr.write(baos, model, RDFFormat.TURTLE);
        String input = ((ByteArrayOutputStream) baos).toString("UTF-8");

        FileBody fileBody = new FakeFileBody(input);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("model", fileBody);

        HttpEntity entity = builder.build();

        request.setEntity(entity);
        HttpResponse response = client.execute(request);

        if(response.getStatusLine().getStatusCode() >= 500) {
            String content = IOUtils.toString(response.getEntity().getContent());
            System.err.println(content);
            System.exit(-1);
        }

        String id = new BasicResponseHandler().handleResponse(response);

        return id;
    }


    public static boolean delete(String operation, String id) throws Exception
    {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete delete = new HttpDelete(uri + operation + URLEncoder.encode(id, "UTF-8"));
        HttpResponse response = client.execute(delete);
        client.close();
        if(response.getStatusLine().getStatusCode() < 300) { return true; }
        return false;

    }


    public static Set<String> searchByKeyword(String operation, Set<String> keywords) throws Exception
    {
        CloseableHttpClient client = HttpClients.createDefault();
        String q = "?";
        for(String k : keywords) {
            q += "q=" + URLEncoder.encode(k, "UTF-8") + "&";
        }

        HttpGet httpget = new HttpGet(uri + operation + q);

        Set<String> ids = client.execute(httpget, responseHandlerList);
        return ids;
    }


    public static Model getModelByID(String operation, String id) throws Exception
    {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(uri + operation + URLEncoder.encode(id, "UTF-8"));
        return client.execute(httpget, responseHandlerRDF);
    }


    public static Set<String> getIDsByID(String operation, String id) throws Exception
    {
        HttpGet httpget = new HttpGet(uri + operation + URLEncoder.encode(id, "UTF-8"));
        CloseableHttpClient client = HttpClients.createDefault();
        Set<String> ids = client.execute(httpget, responseHandlerList);
        return ids;
    }
}



class FakeFileBody extends FileBody
{
    private byte[] bytes;


    public FakeFileBody(String input)
    {
        super(new File("."));
        bytes = input.getBytes();
    }


    @Override
    public void writeTo(OutputStream out) throws IOException
    {
        out.write(bytes);
    }


    @Override
    public long getContentLength()
    {
        return bytes.length;
    }
}
