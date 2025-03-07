package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.logging.Logger;

public class ChatController extends HttpServlet {
    final static Logger logger = Logger.getLogger(ChatController.class.getName());

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("ChatController initialized");
    }

//    @Override
//    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.service(req, resp);
//        logger.info("service! service!");
//    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // 인코딩 변경
//        resp.setCharacterEncoding("UTF-8"); // 인코딩 변경
        resp.setContentType("text/html;charset=UTF-8"); // 브라우저로 인식하는 GET
        resp.getWriter().println("한글 작성");

    }
    // 그걸로 조회를 하면 이미지를 가져다주는...

    // POST -> 키워드 -> 이미지를 생성한 다음에 객체 스토리지에 저장
    // 해당 사진을 리턴
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // 인코딩 변경
        resp.setCharacterEncoding("UTF-8"); // 인코딩 변경

        // CORS 헤더 추가
        resp.setHeader("Access-Control-Allow-Origin", "*");  // 모든 origin 허용
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Max-Age", "3600");

        // preflight 요청 처리(OPTIONS 메소드 처리)
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

//        resp.getWriter().println("한글 쓰면 고장남");
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = objectMapper.readValue(req.getInputStream(), Message.class);
//        resp.getWriter().println(message);

        HttpClient httpClient = HttpClient.newHttpClient();
//        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
//        String token = dotenv.get("TOGETHER_API_KEY");
        String token = System.getenv("TOGETHER_API_KEY");
        String prompt = message.content();
        String model1 = "black-forest-labs/FLUX.1-schnell-Free";
        String model2 = "stabilityai/stable-diffusion-xl-base-1.0";
        Random random = new Random();
        double val = random.nextDouble();
        String body = """
                {
                    "model": "%s",
                    "prompt": "%s",
                    "width": 1024,
                    "height": 768,
                    "steps": %d,
                    "n": 1
                }
                """.formatted((val > 0.5 ? model1 : model2), prompt, val > 0.5 ? 4 : 40); // 확률적으로 반반 분산시켰다, 모델에 따라 다른 steps 수.
        try {
            Thread.sleep(5000); // 이렇게 된 이상 5초 대기 시킨다 진짜
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.together.xyz/v1/images/generations"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .headers("Authorization", "Bearer %s".formatted(token),
                        "Content-Type", "application/json"
                )
                .build();

        String result = "";
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();
            logger.info(result);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        resp.getWriter().println(result);
    }

    @Override
    public void destroy() {
        logger.info("잘가");
        super.destroy();
    }
}

record Message(String content) {
}