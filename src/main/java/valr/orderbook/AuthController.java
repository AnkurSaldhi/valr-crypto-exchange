package valr.orderbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @GetMapping("/user/authtoken")
    public String GetToken() {
        return JwtUtil.generateToken();
    }
}
