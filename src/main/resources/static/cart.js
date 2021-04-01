

//<!--scop: adauga un h1 cu mesajul "Nu ați adăugat niciun produs"-->

        var pretTotal=document.getElementsByName("pretTotal")[0].value

        if(parseFloat(pretTotal)===0)
        {
        var h = document.createElement("h1")
        h.style.color='	rgb(255,228,181)'
        h.style.fontSize='250%'
        h.style.marginLeft='10%'
        h.style.marginTop='-10%'

        var t = document.createTextNode("Nu ați adăugat niciun produs!")
        h.appendChild(t)
        document.body.appendChild(h)
        }


//<!--       dezactiveaza butonul daca pretul comenzii este zero-->

        var pretTotal=document.getElementsByName("pretTotal")[0].value
        var btn_sendOrder=document.getElementById("btn_sendOrder")
        if(parseFloat(pretTotal)===0){
        btn_sendOrder.disabled=true
        btn_sendOrder.style.color='rgb(255,0,0)'
        }



//<!--vouchere-->

            var ourRequest=new XMLHttpRequest()
            ourRequest.open('GET','http://localhost:3081/vouchers')
            ourRequest.onload=function(){

            var ourData = JSON.parse(ourRequest.response);
            var button=document.getElementById("myBtn");
            button.onclick=applyVoucher;

            function applyVoucher() {

            document.getElementsByName("cod_voucher")[0].value=document.getElementsByName("codVoucher")[0].value; <!--update bd user-->

            var request=new XMLHttpRequest()
            request.open('GET','http://localhost:3080/users')
            request.onload=function(){

            var data = JSON.parse(request.response)
            var voucherAcceptat=true;
            var user_id=document.getElementsByName("user_id")[0].value
            var codVoucher=document.getElementsByName("codVoucher")[0].value  <!-- preia inputul unde se scrie codul voucherului -->

            for(var i=0;i<data.length;i++){
            if(data[i].id===parseInt(user_id) && data[i].voucher_used===codVoucher){
            voucherAcceptat=false;
            }}

//<!--     aplica voucher:    -->

            var voucherIntrodusCorect=false;
            var valPretTotal=document.getElementsByName("pretTotal")[0].value

            for(var i=0;i<ourData.length;i++){
            var today = new Date();
            var dataBegin=new Date(ourData[i].dataBegin)
            var dataEnd=new Date(ourData[i].dataEnd)

            if(voucherAcceptat===true && codVoucher===ourData[i].voucherCode && dataBegin<today && today<dataEnd){

            voucherIntrodusCorect=true;
            var refPretRedus=document.getElementById("pretTotal")
            var valPretRedus=(valPretTotal-valPretTotal*ourData[i].reduction/100).toFixed(2)
            refPretRedus.innerText=valPretRedus
            document.getElementsByName("pretTotalTransmis")[0].value=valPretRedus
            }}

            if(voucherIntrodusCorect===false){
            alert("Voucher neacceptat")
//            document.getElementsByName("pretTotalTransmis")[0].value=valPretTotal.toFixed(2)
            }}
            request.send()
            }}
            ourRequest.send()