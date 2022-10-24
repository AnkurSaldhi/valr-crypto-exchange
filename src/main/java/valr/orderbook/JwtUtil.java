package valr.orderbook;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(MarketController.class);
    private static final int JWT_TOKEN_VALIDITY = 365*24*60*60;

    //For demo purpose, these values are hardcoded
    private static final String secret="valrsecret";
    private static final String valrUsername="valruser999";


    public static String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //generate token for user
    public static String generateToken() {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, valrUsername);
    }

    private static String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000L))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    //validate token
    public static Boolean validateToken(String token) {
        final String username = getUsernameFromToken(token);
        return (username.equals(valrUsername) && !isTokenExpired(token));
    }

    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieving any information from token we will need the secret key
    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //retrieve expiration date from jwt token
    public static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    static boolean validateAuthenticatedRequest(String valrToken){
        boolean validUser=false;
        try {
            validUser = JwtUtil.validateToken(valrToken);
        }catch (Exception e){
            logger.info("Jwt verification failed {}",e.getMessage());
        }
        return validUser;

    }
}
