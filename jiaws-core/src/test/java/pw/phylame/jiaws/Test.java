package pw.phylame.jiaws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.Cookie;

import lombok.val;
import pw.phylame.jiaws.core.Assembly;
import pw.phylame.jiaws.io.TextContentSource;
import pw.phylame.jiaws.spike.http.HttpResponse;

public class Test {
    public static void main(String[] args) throws IOException {
        val out = new ByteArrayOutputStream();
        val response = new HttpResponse();
        val content = new TextContentSource("hello world", "gbk", "text/plain");
        response.setServerAssembly(new Assembly());
        response.setContent(content);
        response.addCookie(new Cookie("name", "pw"));
        response.addHeader("name", "pw");
        response.addIntHeader("age", 12);
        response.addDateHeader("from", new Date().getTime());
        response.renderTo(out);
        System.out.println(out.toString());
    }
}
