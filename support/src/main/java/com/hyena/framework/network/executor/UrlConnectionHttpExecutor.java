//package com.hyena.framework.network.executor;
//
//import android.text.TextUtils;
//
//import com.hyena.framework.clientlog.LogUtil;
//import com.hyena.framework.debug.DebugUtils;
//import com.hyena.framework.network.HttpError;
//import com.hyena.framework.network.HttpExecutor;
//import com.hyena.framework.network.HttpListener;
//import com.hyena.framework.network.HttpResult;
//import com.hyena.framework.network.NetworkProvider;
//import com.hyena.framework.network.NetworkSensor;
//import com.hyena.framework.network.utils.EntityUtil;
//import com.hyena.framework.network.utils.HttpUtils;
//
//import org.apache.http.Header;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.conn.params.ConnRouteParams;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.zip.GZIPInputStream;
//
///**
// * Created by yangzc on 16/7/12.
// */
//public class UrlConnectionHttpExecutor implements HttpExecutor {
//
//    private static final int DEFAULT_TIMEOUT = 30;
//
//    @Override
//    public HttpResult doGet(String url, HttpRequestParams params, HttpListener listener) {
//        debug("method: Get , execute :" + url);
//        int timeout = DEFAULT_TIMEOUT;
//        //如果外部没有设置超时时间，则使用默认30s超时
//        if(params != null && params.mTimeout > 0)
//            timeout = params.mTimeout;
//
//        HttpResult result = new HttpResult();
//        result.mHttpListener = listener;
//
//        //检查网络
//        NetworkSensor networkSensor = NetworkProvider.getNetworkProvider().getNetworkSensor();
//        if(networkSensor == null || !networkSensor.isNetworkAvailable()){
//            result.mErrorCode = HttpError.ERROR_NO_AVAILABLE_NETWORK;
//            if(listener != null)
//                try {
//                    listener.onError(HttpError.ERROR_NO_AVAILABLE_NETWORK);
//                } catch (Throwable e1) {
//                    e1.printStackTrace();
//                }
//            return result;
//        }
//
//        if(TextUtils.isEmpty(url)){
//            if(listener != null)
//                try {
//                    listener.onError(HttpError.ERROR_URL_EMPTY);
//                } catch (Throwable e1) {
//                    e1.printStackTrace();
//                }
//            result.mErrorCode = HttpError.ERROR_URL_EMPTY;
//            return result;
//        }
//
//        InputStream is = null;
//        HttpURLConnection conn = null;
//        try {
//            if(listener != null && !listener.onReady(url)){
//                result.mErrorCode = HttpError.ERROR_CANCEL_READY;
//                return result;
//            }
//            //重新构筑URL
//            String new_url = networkSensor.rebuildUrl(url);
//            if(!TextUtils.isEmpty(new_url)){
//                url = new_url;
//            }
//
//            long start = System.currentTimeMillis();
//            result.mUrl = url;//同步请求的URL
//
//            //重新定义请求字符串
//            if(params != null && params.mParams != null)
//                url = HttpUtils.encodeUrl(url, params.mParams);
//
//            HttpHost httpHost = networkSensor.getProxyHost(url, params.isProxy);
//            if (httpHost != null) {
//                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpHost.getHostName(), httpHost.getPort()));
//                conn = (HttpURLConnection) new URL(url).openConnection(proxy);
//            } else {
//                conn = (HttpURLConnection) new URL(url).openConnection();
//            }
//
//
//            long startPos = 0;
//            if(params != null && params.mStartPos > 0){
//                startPos = params.mStartPos;
//                conn.addRequestProperty("RANGE", "bytes=" + startPos + "-");
//                result.mStartPos = startPos;
//            }
//
//            //添加默认头
//            List<NameValuePair> commonHeaders = networkSensor.getCommonHeaders(url, params.isProxy);
//            if(commonHeaders != null){
//                for(NameValuePair pair: commonHeaders){
//                    conn.addRequestProperty(pair.getName(), pair.getValue());
//                }
//            }
//            //添加自定义头
//            if(params != null && params.mHeader != null){
//                Iterator<String> iterator = params.mHeader.keySet().iterator();
//                while(iterator.hasNext()){
//                    String name = iterator.next();
//                    String value = params.mHeader.get(name);
//                    conn.addRequestProperty(name, value);
//                }
//            }
//
//            start = System.currentTimeMillis();
//
//            conn.setRequestMethod("GET");
//            conn.setDoInput(false);
//            conn.setDoOutput(true);
//            conn.connect();
//
//            HttpResponse response = client.execute(request);
//            if(listener != null && listener.onResponse(response)){
//                result.mErrorCode = HttpError.ERROR_CANCEL_RESPONSE;
//                return result;
//            }
//
//            int statusCode = response.getStatusLine().getStatusCode();
//            result.mReqTs = System.currentTimeMillis() - start;//请求响应时间
//            result.mStatusCode = statusCode;//同步状态码
//
//            debug("statusCode :" + statusCode);
//            if(statusCode == HttpStatus.SC_OK
//                    || statusCode == HttpStatus.SC_PARTIAL_CONTENT){
//
//                if(LogUtil.isDebug()){
//                    Header headers[] = response.getAllHeaders();
//                    if(headers != null && headers.length > 0){
//                        for(int i=0; i< headers.length; i++){
//                            debug("header name: " + headers[i].getName() + ", value: " + headers[i].getValue());
//                        }
//                    }
//                }
//
//                long contentLength = response.getEntity().getContentLength();
//                result.mContentLength = contentLength;//同步网络数据长度
//
//                Header encoder = response.getLastHeader("Transfer-Encoding");
//                if(encoder != null){
//                    if("chunked".equalsIgnoreCase(encoder.getValue())){
//                        result.mIsTrunked = true;
//                    }
//                }
//
//                boolean isGzip = EntityUtil.isGZIPed(response.getEntity());
//                result.mIsGzip = isGzip;
//                debug("contentLength : " + contentLength + ", trunked : " + result.mIsTrunked + ", gzip: " + result.mIsGzip);
//
//				/* 通知开始下载 */
//                if(listener != null){
//                    if(!listener.onStart(startPos, contentLength)){
//                        //开始时Cancel掉Http请求
//                        result.mErrorCode = HttpError.ERROR_CANCEL_BEGIN;
//                        return result;
//                    }
//                }
//
//                if(isGzip){
//                    is = new GZIPInputStream(response.getEntity().getContent());
//                }else{
//                    is = response.getEntity().getContent();
//                }
//
//                int len = -1;
//                //控制buffer长度
//                int bufferSize = 1024 * 10;
//                if(params != null && params.mBufferSize > 0){
//                    bufferSize = params.mBufferSize;
//                }
//                byte buf[] = new byte[bufferSize];
//                start = System.currentTimeMillis();
//                while((len = is.read(buf, 0, bufferSize)) > 0){
//                    if(listener != null){
//                        if(!listener.onAdvance(buf, 0, len)){
//                            //正在下载过程中Cancel掉Http请求
//                            result.mErrorCode = HttpError.ERROR_CANCEL_ADVANCE;
//                            break;
//                        }
//                    }
//                    if(networkSensor != null){
//                        networkSensor.updateFlowRate(len);
//                    }
//                }
//                result.mReadTs = System.currentTimeMillis() - start;//读取时间
//                if(listener != null && result.isSuccess()){
//                    listener.onCompleted();
//                }
//            }else{
//                result.mErrorCode = HttpError.ERROR_STATUS_CODE;
//                if(listener != null)
//                    listener.onError(statusCode);
//            }
//        } catch (ClientProtocolException e) {
//            if(listener != null)
//                try {
//                    listener.onError(HttpError.ERROR_UNKNOWN);
//                } catch (Throwable e1) {
//                    e1.printStackTrace();
//                }
//            result.mErrorCode = HttpError.ERROR_UNKNOWN;
//            e.printStackTrace();
//        } catch (IOException e) {
//            if(listener != null)
//                try {
//                    listener.onError(HttpError.ERROR_UNKNOWN);
//                } catch (Throwable e1) {
//                    e1.printStackTrace();
//                }
//            result.mErrorCode = HttpError.ERROR_UNKNOWN;
//            e.printStackTrace();
//        } catch (Throwable e){
//            if(listener != null)
//                try {
//                    listener.onError(HttpError.ERROR_UNKNOWN);
//                } catch (Throwable e1) {
//                    e1.printStackTrace();
//                }
//            result.mErrorCode = HttpError.ERROR_UNKNOWN;
//            e.printStackTrace();
//        } finally {
//            //关闭流
//            if(is != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
//                try {
//                    is.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            //关闭请求
//            if(request != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
//                try{
//                    request.abort();
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//            //关闭连接池中的所有连接,不了解应用中是否存在重用连接的情况
//            //现在改用ThreadSafeClientConnManager故不需要关闭连接管理器
//            if(client != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
//                try {
//                    client.getConnectionManager().shutdown();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //通知结束,开始销毁
//            if(listener != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
//                try {
//                    listener.onRelease();
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        debug("isCancel : " + result.isCanceled() + ", reason :" + result.getCancelReason());
//        return result;
//    }
//
//    @Override
//    public HttpResult doPost(String url, HttpRequestParams params, HttpListener httpListener) {
//        return null;
//    }
//
//    private void debug(String str){
//        if(LogUtil.isDebug()){
//            LogUtil.v("", str);
//        }
//        DebugUtils.debugTxt(str);
//    }
//}
