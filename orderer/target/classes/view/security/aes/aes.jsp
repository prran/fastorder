<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
    <script type="text/javascript" src="../view/security/aes/aes.js"></script>
    <script type="text/javascript" src="../view/security/aes/pbkdf2.js"></script>
    <script type="text/javascript">
        window.addEventListener("message", e=>{
            var iv = CryptoJS.lib.WordArray.random(128/8).toString(CryptoJS.enc.Hex);
            var salt = CryptoJS.lib.WordArray.random(128/8).toString(CryptoJS.enc.Hex);
            var passPhrase = CryptoJS.lib.WordArray.random(128/8).toString(CryptoJS.enc.Hex);
            var plainText = e.data.value;
            var keySize = 128;
            var iterationCount = 1000;
            var key128Bits = CryptoJS.PBKDF2(passPhrase, 
                    CryptoJS.enc.Hex.parse(salt), 
                    { keySize: keySize / 32, iterations: iterationCount }
            );
            var encrypted = CryptoJS.AES.encrypt(
            plainText,
            key128Bits,
            { iv: CryptoJS.enc.Hex.parse(iv) });
            window.parent.postMessage(
                {encrypt : encrypted.toString(), iv : iv , salt : salt, passPhrase : passPhrase},'*'
            );
        })

    </script>
</head>
<body>
    aes
</body>
</html>