package pw.phylame.jiaws.spike.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.NonNull;
import lombok.val;
import pw.phylame.jiaws.io.ChannelInputStream;
import pw.phylame.jiaws.servlet.JiawsHttpRequest;
import pw.phylame.jiaws.spike.ChannelInput;
import pw.phylame.jiaws.spike.ProtocolParser;
import pw.phylame.jiaws.util.ImplementUtils;
import pw.phylame.jiaws.util.ProtocolException;
import pw.phylame.ycl.value.Pair;

public class Http11ChannelParser implements ProtocolParser<HttpServletRequest, HttpServletResponse, ChannelInput> {

    @Override
    public Pair<HttpServletRequest, HttpServletResponse> parse(@NonNull ChannelInput input)
            throws IOException, ProtocolException {
        val channel = input.getChannel();
        val request = new JiawsHttpRequest();
        val in = new ChannelInputStream(channel);
        val hr = HttpRequest.from(in);
        // val response = new JiawsHttpResponse(null);

        return ImplementUtils.raiseForImpl();
    }

}
