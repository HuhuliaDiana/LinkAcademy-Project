//<!--    setez sa nu se afiseze filtrele nealese-->

var btnPrice=document.getElementsByName("btnPrice")[0].value;
var btnBrand=document.getElementsByName("btnBrand")[0].value;
var btnMemRAM=document.getElementsByName("btnMemRAM")[0].value;

var array_btn=[btnPrice, btnBrand, btnMemRAM]
var input_filters=document.getElementsByName("input_filters")

for(let i=0;i<array_btn.length;i++){
if(array_btn[i]==="0"){
input_filters[i].style.display='none'
}}

for(let i=0;i<array_btn.length;i++){
if(input_filters[i].style.display==='none' && array_btn[i]!="0"){
input_filters[i].style.display='inherit';
}}



//<!--afiseaza 'Nu exista produse' pt un nr null de produse-->

var numberCartProducts=document.getElementsByName("numberCartProducts")[0].value
if(parseInt(numberCartProducts)===0){
var h = document.createElement("h2")
h.style.color='	rgb(255,228,181)'
h.style.fontSize='250%';
h.style.textAlign='center';

var t = document.createTextNode("Nu există produse!")
h.appendChild(t)
document.body.appendChild(h)
}


//<!--dau valori pentru btnPrice/Brand/MemRAM in fc de filtrele alese de utilizator-->

var btnApplyFilters=document.getElementsByName("applyFilters")[0];
btnApplyFilters.onclick=giveBtnValues;

var btnPrice=document.getElementsByName("btnPrice")[0];
var btnBrand=document.getElementsByName("btnBrand")[0];
var btnMemRAM=document.getElementsByName("btnMemRAM")[0];

function giveBtnValues(){

var formChoicePrice=document.getElementById("formChoicePrice");
var formChoiceBrand=document.getElementById("formChoiceBrand");
var formChoiceMemRAM=document.getElementById("formChoiceMemRAM");

if(formChoicePrice.options[formChoicePrice.selectedIndex].value==="decreasing-price")
{ btnPrice.value="decreasing-price"}
else if (formChoicePrice.options[formChoicePrice.selectedIndex].value==="rising-price")
{ btnPrice.value="rising-price"}

if(formChoiceBrand.options[formChoiceBrand.selectedIndex].value==="type-samsung")
{ btnBrand.value="SAMSUNG"}
else if (formChoiceBrand.options[formChoiceBrand.selectedIndex].value==="type-huawei")
{ btnBrand.value="HUAWEI"}
else if (formChoiceBrand.options[formChoiceBrand.selectedIndex].value==="type-xiaomi")
{ btnBrand.value="XIAOMI"}
else if (formChoiceBrand.options[formChoiceBrand.selectedIndex].value==="type-lenovo")
{ btnBrand.value="LENOVO"}
else if (formChoiceBrand.options[formChoiceBrand.selectedIndex].value==="type-asus")
{ btnBrand.value="ASUS"}

if (formChoiceMemRAM.options[formChoiceMemRAM.selectedIndex].value==="6GB")
{ btnMemRAM.value="6GB"}
else if (formChoiceMemRAM.options[formChoiceMemRAM.selectedIndex].value==="8GB")
{ btnMemRAM.value="8GB"}
else if (formChoiceMemRAM.options[formChoiceMemRAM.selectedIndex].value==="12GB")
{ btnMemRAM.value="12GB"}
else if (formChoiceMemRAM.options[formChoiceMemRAM.selectedIndex].value==="16GB")
{ btnMemRAM.value="16GB"}

}

