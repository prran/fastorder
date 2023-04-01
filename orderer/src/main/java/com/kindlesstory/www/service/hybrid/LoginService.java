package com.kindlesstory.www.service.hybrid;

import com.kindlesstory.www.exception.StolenedTokenException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import com.kindlesstory.www.data.dto.TokenSet;
import java.time.LocalDate;
import com.kindlesstory.www.exception.DatabaseException;
import com.kindlesstory.www.exception.FormatDismatchException;
import java.util.NoSuchElementException;
import com.kindlesstory.www.exception.OutcastUserException;
import com.kindlesstory.www.data.jpa.table.User;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import com.kindlesstory.www.data.vo.global.Key;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import com.kindlesstory.www.exception.PermissionException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.kindlesstory.www.data.jpa.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.module.Crypt;
import org.springframework.stereotype.Service;
import com.kindlesstory.www.service.inter.SecurityService;
import com.kindlesstory.www.data.cast.UseAuth;

@Service
public class LoginService extends UseAuth implements SecurityService
{
    private static final int ONE_DAY = 86400000;
    @Autowired
    private Crypt crypt;
    @Autowired
    private UserRepository userRepository;
    
    public String authorization(final Map<String, String> value, final HttpServletRequest request) throws PermissionException {
        final boolean loginflag = indexClassification(value);
        if (loginflag) {
            return this.sessionIdAuthorization(value.get("sessionId"), request);
        }
        return this.tokenAuthorization(value.get("token"));
    }
    
    private String sessionIdAuthorization(final String sessionId, final HttpServletRequest request) throws PermissionException {
        final String accountIp = crypt.decryptRsa(sessionId);
        if (accountIp == null) {
            throw new PermissionException();
        }
        final String clientIp = getClientIp(request);
        if (accountIp.equals(clientIp) || clientIp.equals("127.0.0.1")) {
            return crypt.encryptSha256(accountIp);
        }
        throw new PermissionException();
    }
    
    private String getClientIp(final HttpServletRequest request) {
        String ip = null;
        final String[] ipInfos = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };
        String[] array;
        for (int length = (array = ipInfos).length, i = 0; i < length; ++i) {
            final String ipInfo = array[i];
            ip = request.getHeader(ipInfo);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    private String tokenAuthorization(final String token) throws PermissionException {
        try {
            final Claims claims = Jwts.parser().setSigningKey(Key.getJWTKey().getBytes()).parseClaimsJws(token).getBody();
            final String tokenId = claims.getSubject();
            final String passId = this.crypt.encryptSha256(tokenId);
            return passId;
        }
        catch (ExpiredJwtException e3) {
            throw new PermissionException();
        }
        catch (NullPointerException e4) {
            throw new NullPointerException();
        }
        catch (SignatureException | MalformedJwtException | UnsupportedJwtException ex2) {
            throw new PermissionException();
        }
    }
    
    public boolean authentication(final Object index) throws OutcastUserException, FormatDismatchException, DatabaseException {
        boolean result = false;
        try {
            final String originalIndex = crypt.decryptRsa(index);
            final String comparator = crypt.encryptSha256(originalIndex);
            try {
                final User user = this.userRepository.findById(comparator).get();
                if (user.getUserBan()) {
                    throw new OutcastUserException();
                }
                result = true;
            }
            catch (NoSuchElementException ex) {}
        }
        catch (NullPointerException e) {
            throw new FormatDismatchException();
        }
        catch (Exception e2) {
            throw new DatabaseException();
        }
        return result;
    }
    
    public boolean indexClassification(final Map<String, String> value) throws FormatDismatchException {
        try {
            final String sessionId = value.get("sessionId");
            final String token = value.get("token");
            boolean loginflag = true;
            if (sessionId.equals("null") || sessionId == null || sessionId.equals("")) {
                if (token.equals("null") || token == null) {
                    throw new FormatDismatchException();
                }
                loginflag = false;
            }
            else if (!token.equals("null") && token != null && !token.equals("")) {
                throw new FormatDismatchException();
            }
            return loginflag;
        }
        catch (NullPointerException e) {
            throw new FormatDismatchException();
        }
    }
    
    public void createAccount(final Object index) throws FormatDismatchException, DatabaseException {
        final String originalIndex = crypt.decryptRsa(index);
        final String[] informationArea = originalIndex.split("\\.");
        if (informationArea.length > 4) {
            throw new FormatDismatchException();
        }
        try {
            String[] array;
            for (int length = (array = informationArea).length, i = 0; i < length; ++i) {
                final String info = array[i];
                final int area = Integer.parseInt(info);
                if (area > 255 || area < 0) {
                    throw new FormatDismatchException();
                }
            }
        }
        catch (NumberFormatException e) {
            throw new FormatDismatchException();
        }
        try {
            final String joinDate = LocalDate.now().toString().replace("-", "");
            final String userName = String.valueOf(index.toString().substring(0, 3)) + joinDate;
            final String userId = crypt.encryptSha256(originalIndex);
            final User user = new User();
            user.setUserBan(false);
            user.setUserId(userId);
            user.setUserName(userName);
            this.userRepository.save(user);
        }
        catch (Exception e2) {
            throw new DatabaseException();
        }
    }
    
    public String createTokenAccount() throws DatabaseException {
        final String id = "token" + System.currentTimeMillis() + Math.random();
        final String middleEncodeId = crypt.encryptMD5(id);
        final String passId = crypt.encryptSha256(middleEncodeId);
        try {
            final String joinDate = LocalDate.now().toString().replace("-", "");
            final String userName = String.valueOf(middleEncodeId.toString().substring(0, 3)) + joinDate;
            final User user = new User();
            user.setUserBan(false);
            user.setUserId(passId);
            user.setUserName(userName);
            this.userRepository.save(user);
            return middleEncodeId;
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public TokenSet getToken(final Object subject) {
        final String id = crypt.decryptRsa(subject);
        final JwtBuilder builder = Jwts.builder().setHeaderParam("typ", (Object)"JWT").setExpiration(new Date(System.currentTimeMillis() + ONE_DAY)).setIssuedAt(new Date(System.currentTimeMillis())).signWith(SignatureAlgorithm.HS256, Key.getJWTKey().getBytes());
        final String refreshToken = builder.compact();
        builder.setExpiration(new Date(System.currentTimeMillis() + 35000L)).setIssuedAt(new Date(System.currentTimeMillis())).setSubject(id);
        final String accessToken = builder.compact();
        final TokenSet tokenSet = new TokenSet();
        tokenSet.setAccessToken(accessToken);
        tokenSet.setRefreshToken(refreshToken);
        return tokenSet;
    }
    
    public String refreshToken(final Map<String, String> data) throws PermissionException, StolenedTokenException {
        try {
            final Claims claims = Jwts.parser().setSigningKey(Key.getJWTKey().getBytes()).parseClaimsJws(data.get("accessToken")).getBody();
            int lefttime = (int)(claims.getExpiration().getTime() - System.currentTimeMillis());
            if (lefttime > 5000) {
                throw new StolenedTokenException();
            }
            lefttime = (int)(System.currentTimeMillis() - claims.getIssuedAt().getTime());
            if (lefttime < 30000) {
                throw new StolenedTokenException();
            }
            Jwts.parser().setSigningKey(Key.getJWTKey().getBytes());
            final JwtBuilder builder = Jwts.builder().setHeaderParam("typ", (Object)"JWT").setExpiration(new Date(System.currentTimeMillis() + 32000L)).setIssuedAt(new Date(System.currentTimeMillis())).setSubject(claims.getSubject()).signWith(SignatureAlgorithm.HS256, Key.getJWTKey().getBytes());
            final String newAccessToken = builder.compact();
            return newAccessToken;
        }
        catch (StolenedTokenException ex) {
            throw new StolenedTokenException();
        }
        catch (Exception e) {
            throw new PermissionException();
        }
    }
}