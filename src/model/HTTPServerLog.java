package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HTTPServerLog {
        
	// Log记录范例：
	// 199.72.81.55 - - [01/Jul/1995:00:00:01 -0400] "GET /history/apollo/ HTTP/1.0" 200 6245
    
	private String remoteAddr;			// 记录客户端的host地址			e.g. 199.72.81.55
    private String timeLocal;           // 记录访问时间与时区				e.g. 01/Jul/1995:00:00:01 -0400
    private String requestVerb;         // 记录请求动作					e.g. GET
    private String requestUrl;          // 记录请求的url					e.g. /history/apollo/
    private String requestProtocol;     // 记录请求的http协议				e.g. HTTP/1.0
    private String status;              // 记录请求状态；成功是200			e.g. 200
    private String bodyBytes;           // 记录发送给客户端文件主体内容大小	e.g. 6245

    private boolean valid = true;       // 判断数据是否合法
    private Date timeLocalDate;         // timeLocal对应的Date对象
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Valid:\t\t" + this.valid);
        sb.append("\nRemote Address:\t" + this.remoteAddr);
        sb.append("\nTime Local:\t" + this.timeLocal);
        sb.append("\nHTTP Verb:\t" + this.requestVerb);
        sb.append("\nHTTP Protocol:\t" + this.requestProtocol);
        sb.append("\nRequest URL:\t" + this.requestUrl);
        sb.append("\nStatus:\t\t" + this.status);
        sb.append("\nReturned Bytes:\t" + this.bodyBytes);
        return sb.toString();
    }

    /**
     * 返回用户访问时间的Date对象
     * @return
     */
    public Date getTimeLocalDate() {
    	if(timeLocalDate == null){
    		if(timeLocal == null){
    			return null;
    		}
                    
	        try {
	        	SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
	        	timeLocalDate = df.parse(timeLocal);
	        } 
	        catch (ParseException e) {
	        	e.printStackTrace();
	        }
    	}
    	return timeLocalDate;
    }

    /**
     * 返回用户访问时间精确到小时的字符串，例如：2013年11月24日下午1点，2013112413
     * @return
     */
    public String getTimeLocalDateByHour(){
    	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
    	return df.format(this.getTimeLocalDate());
    }
        
    public static HTTPServerLog parse(String logLine) {
        
    	HTTPServerLog log = new HTTPServerLog();
    	
    	try{
	        String[] remoteAddr = logLine.split(" - - \\[");
	        log.setRemoteAddr(remoteAddr[0]);
	        
	        String[] timeLocal = remoteAddr[1].split("\\] ");
	        log.setTimeLocal(timeLocal[0]);
	        
	        int bodyBytesStart = timeLocal[1].lastIndexOf(" ");
	        log.setBodyBytes(timeLocal[1].substring(bodyBytesStart) + 1);
	        
	        int statusStart = timeLocal[1].lastIndexOf("\" ");
	        log.setStatus(timeLocal[1].substring(statusStart + 2, bodyBytesStart));
	        
	        if(Integer.parseInt(log.getStatus()) >= 400){
	        	log.setValid(false);
	        	return log;
	        }
	        
	        String request = timeLocal[1].substring(1, statusStart);
	        
	        int verbEnd = request.indexOf(" ");
	        int urlEnd = request.lastIndexOf(" ");
	        if(verbEnd == urlEnd){
		        log.setRequestVerb(request.substring(0, verbEnd));
		        log.setRequestUrl(request.substring(verbEnd + 1));
	        }
	        else{
		        log.setRequestVerb(request.substring(0, verbEnd));
		        log.setRequestUrl(request.substring(verbEnd + 1, urlEnd));
		        log.setRequestProtocol(request.substring(urlEnd + 1));
	        }
    	}
    	catch(Exception e){
    		System.err.println("Invalid log line: " + logLine);
    		log.setValid(false);
    		return log;
    	}
    	
        return log;
    }
        
    public static void main(String[] args){
    	String logLine = "pc302b.svznov.kemerovo.su - - [01/Jul/1995:05:28:55 -0400] \"GET /shuttle/missions/sts-71/images/images.html HTTP/1.0\" 200 7634";
        System.out.println(logLine + "\n");
        
        HTTPServerLog log = HTTPServerLog.parse(logLine);
        System.out.println(log + "\n");
        
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//        System.out.println(df.format(log.getTimeLocalDate()));
//        System.out.println(log.getTimeLocalDateByHour());
    }
    

    /* geters and setters */
    
    public String getRemoteAddr() {
    	return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
    	this.remoteAddr = remoteAddr;
    }

    public String getTimeLocal() {
    	return timeLocal;
    }

    public void setTimeLocal(String timeLocal) {
    	this.timeLocal = timeLocal;
    }

    public String getRequestVerb() {
    	return requestVerb;
    }

    public void setRequestVerb(String requestVerb) {
    	this.requestVerb = requestVerb;
    }

    public String getRequestUrl() {
    	return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
    	this.requestUrl = requestUrl;
    }

    public String getRequestProtocol() {
    	return requestProtocol;
    }

    public void setRequestProtocol(String requestProtocol) {
    	this.requestProtocol = requestProtocol;
    }

    public String getStatus() {
    	return status;
    }

    public void setStatus(String status) {
    	this.status = status;
    }

    public String getBodyBytes() {
    	return bodyBytes;
    }

    public void setBodyBytes(String bodyBytes) {
    	this.bodyBytes = bodyBytes;
    }

    public boolean isValid() {
    	return valid;
    }

    public void setValid(boolean valid) {
    	this.valid = valid;
    }
}