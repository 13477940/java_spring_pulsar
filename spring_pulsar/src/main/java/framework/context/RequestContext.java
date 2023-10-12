package framework.context;

import framework.controller.BaseRestController;
import framework.observer.Handler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用於替代舊版本的 AsyncActionContext 角色
 */
public class RequestContext extends BaseRestController {

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected String service_name;

    protected Handler state_handler;

    // 採用 private 強制只能從 builder 建立
    private RequestContext(
            HttpServletRequest _request,
            HttpServletResponse _response,
            String _service_name,
            Handler _state_handler
    ) {
        {
            request = _request;
            response = _response;
            service_name = _service_name;
            state_handler = _state_handler;
        }
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getServiceName() {
        return service_name;
    }

    // 以 builder 形式方便後續追加功能
    public static class Builder {

        private HttpServletRequest request;
        private HttpServletResponse response;

        private String service_name = null; // 可以直接識別任務的特徵值

        private Handler handler = null;

        public RequestContext.Builder setHttpServletRequest(HttpServletRequest _request) {
            request = _request;
            return this;
        }

        public RequestContext.Builder setHttpServletResponse(HttpServletResponse _response) {
            response = _response;
            return this;
        }

        public RequestContext.Builder setServiceName(String _service_name) {
            service_name = _service_name;
            return this;
        }

        public RequestContext.Builder setStateHandler(Handler _handler) {
            handler = _handler;
            return this;
        }

        public RequestContext build() {
            return new RequestContext(request, response, service_name, handler);
        }

    }

}
