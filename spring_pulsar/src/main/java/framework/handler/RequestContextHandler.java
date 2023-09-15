package framework.handler;

import framework.context.RequestContext;
import framework.controller.BaseRestController;
import framework.observer.Bundle;
import framework.observer.Handler;
import framework.observer.Message;

/**
 * 適用於 HttpServletRequest, HttpServletResponse 處理任務的責任鏈節點原型
 */
public abstract class RequestContextHandler extends BaseRestController {

    private RequestContextHandler nextHandler;
    private Handler nonMatchedExceptionHandler;

    /**
     * Handler 處理事件起始點，通常直接呼叫 checkIsMyJob()
     */
    public abstract void startup(RequestContext requestContext);

    /**
     * Handler 確認是否為自身的工作
     */
    protected abstract boolean checkIsMyJob(RequestContext requestContext);

    /**
     * 設定下一位 Handler
     */
    public void setNextHandler(RequestContextHandler handler) {
        this.nextHandler = handler;
        if(null != this.nonMatchedExceptionHandler) {
            this.nextHandler.setNonMatchedExceptionHandler(this.nonMatchedExceptionHandler);
        }
    }

    /**
     * 如果該項工作不屬於這位 Handler 轉交給下一個 Handler
     */
    protected void passToNext(RequestContext requestContext) {
        if(null != nextHandler) {
            this.nextHandler.startup(requestContext);
        } else {
            // 如果是持續遞交到沒有下一個 handler 表示為無效的請求
            if(null != this.nonMatchedExceptionHandler) {
                Bundle b = new Bundle();
                b.putString("status", "fail");
                b.putString("msg", "non_matched");
                b.putString("msg_zht", "沒有符合條件的 handler 進行處理");
                Message m = this.nonMatchedExceptionHandler.obtainMessage();
                m.setData(b);
                this.nonMatchedExceptionHandler.sendMessage(m);
            } else {
                try {
                    throw new Exception("沒有符合條件的 handler 進行處理");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 取得下一位 Handler
     */
    public RequestContextHandler getNextHandler() {
        return this.nextHandler;
    }

    /**
     * 設定沒有被處理的事件例外錯誤回傳處理
     */
    public void setNonMatchedExceptionHandler(Handler handler) {
        setNonMatchedExceptionHandler(handler, false);
    }
    public void setNonMatchedExceptionHandler(Handler handler, boolean overwrite) {
        if(null == this.nonMatchedExceptionHandler) {
            this.nonMatchedExceptionHandler = handler;
        } else {
            if(overwrite) {
                this.nonMatchedExceptionHandler = handler;
            } else {
                try {
                    throw new Exception("已有 handler 時，需附帶 overwrite = true 才能進行覆蓋 handler");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
