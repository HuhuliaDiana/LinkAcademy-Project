
//<!--dezactivez butonul pt produsul ce nu se mai afla in stoc-->


            var buttons=document.getElementsByName("button")
            var refQuantity=document.getElementsByName("quantity")
            var refMsjProdDispon=document.getElementsByName("produsDisponibil")

            for(var i=0;i<refQuantity.length;i++)
            {
            var quantityValue=refQuantity[i].value

            if(parseInt(quantityValue)===0)
            {
            buttons[i].disabled=true
            buttons[i].style.color='rgb(255,0,0)'
            refMsjProdDispon[i].textContent="Acest produs nu se mai găsește în stoc"
            refMsjProdDispon[i].style.color='rgb(128,128,128)'
            refMsjProdDispon[i].style.fontSize='90%'
            }

            else
            {
            refMsjProdDispon[i].textContent="În stoc"
            refMsjProdDispon[i].style.color= 'rgb(255,218,185)'
            refMsjProdDispon[i].style.fontSize='150%'
            }

            refMsjProdDispon[i].style.marginTop='30px'
}