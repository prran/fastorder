package com.kindlesstory.www.validator;

import com.kindlesstory.www.exception.DecryptException;
import com.kindlesstory.www.exception.UnprocessableCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.module.Crypt;
import org.springframework.stereotype.Service;

@Service
public class JavaScriptCodeDeepValidator
{
    private String[] loopRule1 = new String[] { "while ", "for ", "class ", "function ", "%77%68%69%6c%65", "%66%6f%72", "%63%6c%61%73%73", "%66%75%6e%63%74%69%6f%6e", "&#119;&#104;&#105;&#108;&#101;", "&102;&#111;&#114;", "&#99;&#108;&#97;&#115;&#115;", "&#102;&#117;&#110;&#99;&#116;&#105;&#111;&#110;", "&#x77;&#x68;&#x69;&#x6c;&#x65;", "&#x66;&#x6f;&#x72;", "&#x63;&#x6c;&#x61;&#x73;&#x73;", "&#x66;&#x75;&#x6e;&#x63;&#x74;&#x69;&#x6f;&#x6e;" };
    private String[] loopRule2 = new String[] { "\\)=>\\{", "order\\(", "while\\(", "for\\(", "class\\{", "function\\(", "%29%3d%3e%7b", "%6f%72%64%65%72", "&rpar;&equals;&gt;&lcub;", "&#x29;&#x3d;&#x3e;&#x7b;", "&#x6f;&#x72;&#x64;&#x65;&#x72;", "&#41;&#61;&#62;&#123;", "&#111;&#114;&#100;&#101;&#114;" };
    private String[] keywordRule = new String[] { "window.", "windows.", "console.", "alert(", "document.", "require ", "import ", "export ", "axios", "fetch", "ajax", "%77%69%6e%64%6f%77", "%63%6f%6e%73%6f%6c%65", "%61%6c%65r%72%74", "%64%6f%63%75%6d%65%6e%74", "%72%65%71%75%69%72%65", "%69%6d%70%6f%72%74", "%65%78%70%6f%72%74", "%61%78%69%6f%73", "%66%65%74%63%68", "%61%6a%61%78", "&#x77;&#x69;&#x6e;&#x64;&#x6f;&#x77;", "&#x63;&#x6f;&#x6e;&#x73;&#x6f;&#x6c;&#x65;", "&#x61;&#x6c;&#x65;&#x72;&#x74;", "&#x64;&#x6f;&#x63;&#x75;&#x6d;&#x65;&#x6e;&#x74;", "&#x72;&#x65;&#x71;&#x75;&#x69;&#x72;&#x65;", "&#x69;&#x6d;&#x70;&#x6f;&#x72;&#x74;", "&#x65;&#x78;&#x70;&#x6f;&#x72;&#x74;", "&#x61;&#x78;&#x69;&#x6f;&#x73;", "&#x66;&#x65;&#x74;&#x63;&#x68;", "&#x61;&#x6a;&#x61;&#x78;", "&#119;&#105;&#110;&#100;&#111;&#119;", "&#99;&#111;&#110;&#115;&#111;&#108;&#101;", "&#97;&#108;&#101;&#114;&#116;", "&#100;&#111;&#99;&#117;&#109;&#101;&#110;&#116;", "&#114;&#101;&#113;&#117;&#105;&#114;&#101;", "&#105;&#109;&#112;&#111;&#114;&#116;", "&#101;&#120;&#112;&#111;&#114;&#116;", "&#97;&#120;&#105;&#111;&#115;", "&#102;&#101;&#116;&#99;&#104;", "&#97;&#106;&#97;&#120;", "window", "console", "alert", "document", "require", "import", "export", "axios", "fetch", "ajax" };
    private String[] externalRule = new String[] { "<%", "<?", "<py", "#", "$", "%3c%25", "%3c%3f", "%3c%70%79", "%23", "%24", "&lt;&percnt;", "&lt;&quest;", "&lt;&#x70;&#x79;", "&lt;&#112;&#121;", "&num;", "&dollar;", "&#x3c;&#x25;", "&#x3c;&#x3f;", "&#x3c;&#x70;&#x79;", "&#x23;", "&#x24;", "&#60;&#37;", "&#60;&#63;", "&#60;&#112;&#121;", "&#35;", "&#36;", "%23x003c" };
    private String[] tagRule = new String[] { "hidden", "style", "classname", "%68%69%64%64%65%6e", "%73%74%79%6c%65", "%63%6c%61%73%73%6e%61%6d%65", "&#x68;&#x69;&#x64;&#x64;&#x65;&#x6e;", "&#x73;&#x74;&#x79;&#x6c;&#x65;", "&#x63;&#x6c;&#x61;&#x73;&#x73;&#x6e;&#x61;&#x6d;&#x65;", "&#104;&#105;&#100;&#100;&#101;&#110;", "&#115;&#116;&#121;&#108;&#101;", "&#99;&#108;&#97;&#115;&#115;&#110;&#97;&#109;&#101;" };

    @Autowired
    private Crypt crypt;
    
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(String.class);
    }
    
    public String getValidatedContext(String target, String iv, String salt, String passPhrase) throws UnprocessableCodeException, ClassCastException, DecryptException {
        String context = crypt.decryptAes(target, iv, salt, passPhrase);
        String jsCode = context.toLowerCase();
        String noSpaceCode = jsCode.replace(" ", "").replace("\n", "").replace("\t", "");
        if (!loopFilter(jsCode, noSpaceCode) || !keywordFilter(jsCode) || !externalFilter(noSpaceCode) || !tagFilter(noSpaceCode)) {
            throw new UnprocessableCodeException();
        }
        return context;
    }
    
    private boolean loopFilter(String string, String noSpaceString) {
        for (String rule : loopRule1) {
            if (!rule.equals("function ")) {
                if (string.contains(rule)) {
                    return false;
                }
            }
            else if (string.split(rule).length != 2) {
                return false;
            }
        }
        for (String rule : loopRule2) {
            if (rule.equals("function\\(")) {
                if (noSpaceString.contains(rule)) {
                    return false;
                }
            }
            else if (rule.equals("order\\(")) {
                if (noSpaceString.split(rule).length != 2) {
                    return false;
                }
            }
            else if (noSpaceString.contains(rule)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean keywordFilter(String string) {;
        for (String rule : keywordRule) {
            if (string.contains(rule)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean externalFilter(String noSpaceString) {
        for (String rule : externalRule) {
            if (noSpaceString.contains(rule)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean tagFilter(String noSpaceString) {
        if (!noSpaceString.contains("<")) {
            return true;
        }
        if (noSpaceString.contains(">")) {
            int tagStartIndex = -1;
            char[] charArray = noSpaceString.toCharArray();
            for (int i = 0; i < noSpaceString.length(); ++i) {
                if (charArray[i] == '<') {
                    tagStartIndex = i;
                }
                else if (charArray[i] == '>' && tagStartIndex != -1) {
                    String tagString = noSpaceString.substring(tagStartIndex + 1, i);
                    for (String rule : tagRule) {
                        if (tagString.contains(rule)) {
                            return false;
                        }
                    }
                    tagStartIndex = -1;
                }
            }
        }
        return true;
    }
}