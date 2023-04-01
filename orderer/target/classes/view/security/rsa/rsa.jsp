<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
    <script type="text/javascript" src="../view/security/rsa/rsa.js"></script>
    <script type="text/javascript" src="../view/security/rsa/jsbn.js"></script>
    <script type="text/javascript" src="../view/security/rsa/prng4.js"></script>
    <script type="text/javascript" src="../view/security/rsa/rng.js"></script>
    <script type="text/javascript">
        window.addEventListener("message", e=>{
            var rsa = new RSAKey();
            rsa.setPublic(e.data.modulus,e.data.exponent);
            window.parent.postMessage(rsa.encrypt(e.data.value),'*');
        })
    </script>
</head>
<body>
    rsa
</body>
</html>