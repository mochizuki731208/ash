$.fn.sjld = function(shenfen,chengshi,quyu){
	var sfp = shenfen+' p'
	var csp = chengshi+' p'
	var qyp = quyu+' p'
	var sfs = shenfen+' .m_zlxg2'
	var css = chengshi+' .m_zlxg2'
	var qys = quyu+' .m_zlxg2'
	var sfli = shenfen+' ul li'
	var csli = chengshi+' ul li'
	var qyli = quyu+' ul li'
	$('.m_zlxg').click(function(){
		$(this).find('.m_zlxg2').slideDown(200);
	})
	$('.m_zlxg').mouseleave(function(){
		$(this).find('.m_zlxg2').slideUp(200);
	})
	var sfgsmr = provinceList;
	var csgsmr = provinceList[0].cityList;
	var qygsmr = provinceList[0].cityList[0].areaList;
	var kuandu = new Array();
	
	
	$(sfp).text(sfgsmr[0].name);
	$(csp).text(csgsmr[0].name);
	$(qyp).text(qygsmr[0]);
	//默认城市
	for(a=0;a<sfgsmr.length;a++){
		var sfmcmr = sfgsmr[a].name;
		var sfnrmr = "<li>"+sfmcmr+"</li>";
		$(shenfen).find('ul').append(sfnrmr);
	}
	for(b=0;b<csgsmr.length;b++){
		var csmcmr = csgsmr[b].name;
		
		var csnrmr = "<li>"+csmcmr+"</li>";
		$(chengshi).find('ul').append(csnrmr);
		kuandu[b] =csmcmr.length*14+20;
	}
	for(c=0;c<qygsmr.length;c++){
		var qymcmr = qygsmr[c];
		var qynrmr = "<li>"+qymcmr+"</li>";
		$(quyu).find('ul').append(qynrmr);
		$("#sanji").html(qygsmr[0]);
		$(".sanjibtn").colorbox({width:"50%", inline:true, href:"#sanji_list"});
	}
	Array.max=function(array)
		{
    		return Math.max.apply(Math,array);
		}
	var max_kd = Array.max(kuandu); 
		if(max_kd<118){
			$(css).width('118px');
		}
			else{
					$(css).width(max_kd);	
			}
	
/*---------------------------------------------------------------------*/

	$(sfli).click(function(){
		var dqsf = $(this).text();
		$(shenfen).find('p').text(dqsf);
		$(shenfen).find('p').attr('title',dqsf);
		var sfnum = $(this).index();
	var csgs = provinceList[sfnum].cityList;
	var csgs2 = provinceList[sfnum].cityList[0].areaList;	
	$("#sanji").html(csgs2[0]);
	$(".sanjibtn").colorbox({width:"50%", inline:true, href:"#sanji_list"});
	$(chengshi).find('ul').text('');
	var kuandu = new Array();
	for(i=0;i<csgs.length;i++){
		var csmc = csgs[i].name;
		var csnr = "<li>"+csmc+"</li>";
		$(chengshi).find('ul').append(csnr);
		kuandu[i] =csmc.length*14+20;
	}
Array.max=function(array)
{
    return Math.max.apply(Math,array);
}
var max_kd = Array.max(kuandu); 
if(max_kd<91){
	$(css).width('91px');
	}
	else{
	$(css).width(max_kd);	
	}
	var qygsdqmr = provinceList[sfnum].cityList[0].areaList;
	$(quyu).find('ul').text('');
	for(j=0;j<qygsdqmr.length;j++){
		var qymc = qygsdqmr[j];
		var qynr = "<li>"+qymc+"</li>";
		$(quyu).find('ul').append(qynr);
	}		
	$(csp).text(csgs[0].name);
	$(qyp).text(csgs2[0]);
	
	$('#sfdq_num').val(sfnum);

/*------------------*/
	$(csli).click(function(){
		var dqcs = $(this).text();
		var dqsf_num = $('#sfdq_num').val();
		if(dqsf_num==""){
			dqsf_num=0;
			}
			else{
			var dqsf_num = $('#sfdq_num').val();
			}
		$(chengshi).find('p').text(dqcs);
		$(chengshi).find('p').attr('title',dqcs);
		var csnum = $(this).index();
	var qygs = provinceList[dqsf_num].cityList[csnum].areaList;
	$(quyu).find('ul').text('');
	for(j=0;j<qygs.length;j++){
		var qymc = qygs[j];
		var qynr = "<li>"+qymc+"</li>";
		$(quyu).find('ul').append(qynr);
	}
	$("#sanji").html(qygs[0]);
	$(".sanjibtn").colorbox({width:"50%", inline:true, href:"#sanji_list"});
$(qyp).text(qygs[0]);
	$('#csdq_num').val(csnum);
	
	$(this).parents('.m_zlxg2').width(kuandu);
	$(qyli).click(function(){
	var dqqy = $(this).text();
		$(quyu).find('p').text(dqqy);
		$(quyu).find('p').attr('title',dqqy);
			
})//区级
	})	//市级
/*------------------*/	
$(qyli).click(function(){
	var dqqy = $(this).text();
		$(quyu).find('p').text(dqqy);
		$(quyu).find('p').attr('title',dqqy);
			
})//区级


		})//省级
/*---------------------------------------------------------------------*/		
		
		
		
	$(csli).click(function(){
		var dqcs = $(this).text();
		var dqsf_num = $('#sfdq_num').val();
		if(dqsf_num==""){
			dqsf_num=0;
			}
			else{
			var dqsf_num = $('#sfdq_num').val();
			}
		$(chengshi).find('p').text(dqcs);
		$(chengshi).find('p').attr('title',dqcs);
		var csnum = $(this).index();
	var qygs = provinceList[dqsf_num].cityList[csnum].areaList;
	$(quyu).find('ul').text('');
	for(j=0;j<qygs.length;j++){
		var qymc = qygs[j];
		var qynr = "<li>"+qymc+"</li>";
		$(quyu).find('ul').append(qynr);
	}
$(qyp).text(qygs[0]);
	$('#csdq_num').val(csnum);
	/*------------------*/
	$(qyli).click(function(){
	var dqqy = $(this).text();
		$(quyu).find('p').text(dqqy);
		$(quyu).find('p').attr('title',dqqy);

			
})//区级
	})	//市级
/*---------------------------------------------------------------------*/	
	
$(qyli).click(function(){
	var dqqy = $(this).text();
		$(quyu).find('p').text(dqqy);
		$(quyu).find('p').attr('title',dqqy);

			
})//区级

/*---------------------------------------------------------------------*/
$('.m_zlxg').click(function(){
	$('#yiji_tj').val($(sfp).text());
	$('#erji_tj').val($(csp).text());
	
	})//表单传值获取

}


var provinceList = [
{name:'品牌设计', cityList:[		   
{name:'LOGO设计', areaList:['<a class="sanjibtn" target="_blank">选择产品分类</a>']},		   
{name:'VI设计', areaList:['']}
]},

{name:'网站建设', cityList:[
{name:'企业网站建设', areaList:['']},
{name:'门户网站建设', areaList:['<a class="sanjibtn" target="_blank">选择产品分类2</a>']}
]}
];