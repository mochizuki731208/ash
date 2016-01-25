(function($){
    if ($.validator) {
        $.validator.addMethod("alphanumeric", function(value, element) {
            return this.optional(element) || /^\w+$/i.test(value);
        }, "Letters, numbers or underscores only please");

        $.validator.addMethod("lettersonly", function(value, element) {
            return this.optional(element) || /^[a-z]+$/i.test(value);
        }, "Letters only please");

        jQuery.validator.addMethod("nowhitespace", function(value, element) {
            return this.optional(element) || /^\S+$/i.test(value);
        }, "No white space please");

        jQuery.validator.addMethod("integer", function(value, element) {
            return this.optional(element) || /^-?\d+$/.test(value);
        }, "A positive or negative non-decimal number please");

        $.validator.addMethod("phone", function(value, element) {
            return this.optional(element) || /^[0-9 \(\)]{7,30}$/.test(value);
        }, "Please specify a valid phone number");

        $.validator.addMethod("mobile", function(value, element) {
            return this.optional(element) || /^13[0-9]{9}$|14[0-9]{9}$|15[0-9]{9}$|18[0-9]{9}$|17[0-9]{9}$/.test(value);
        }, "Please specify a valid mobile.ftl number");

        $.validator.addMethod("postcode", function(value, element) {
            return this.optional(element) || /^[0-9 A-Za-z]{5,20}$/.test(value);
        }, "Please specify a valid postcode");

        $.validator.addMethod("date", function(value, element) {
            value = value.replace(/\s+/g, "");
            if (String.prototype.parseDate){
                var $input = $(element);
                var pattern = $input.attr('dateFmt') || 'yyyy-MM-dd';

                return !$input.val() || $input.val().parseDate(pattern);
            } else {
                return this.optional(element) || value.match(/^\d{4}[\/-]\d{1,2}[\/-]\d{1,2}$/);
            }
        }, "Please enter a valid date.");

        jQuery.validator.addMethod("time", function(value, element) {
            return this.optional(element) || /^([01]\d|2[0-3])(:[0-5]\d){1,2}$/.test(value);
        }, "Please enter a valid time, between 00:00 and 23:59");
        jQuery.validator.addMethod("time12h", function(value, element) {
            return this.optional(element) || /^((0?[1-9]|1[012])(:[0-5]\d){1,2}(\ ?[AP]M))$/i.test(value);
        }, "Please enter a valid time in 12-hour am/pm format");

        /*自定义js函数验证
         * <input type="text" name="xxx" customvalid="xxxFn(element)" title="xxx" />
         */
        $.validator.addMethod("customvalid", function(value, element, params) {
            try{
                return eval('(' + params + ')');
            }catch(e){
                return false;
            }
        }, "Please fix this field.");

        /**
         * Return true if the field value matches the given format RegExp
         *
         * @example jQuery.validator.methods.pattern("AR1004",element,/^AR\d{4}$/)
         * @result true
         *
         * @example jQuery.validator.methods.pattern("BR1004",element,/^AR\d{4}$/)
         * @result false
         *
         * @name jQuery.validator.methods.pattern
         * @type Boolean
         * @cat Plugins/Validate/Methods
         */
        jQuery.validator.addMethod("pattern", function(value, element, param) {
            if (this.optional(element)) {
                return true;
            }
            if (typeof param === 'string') {
                param = new RegExp('^(?:' + param + ')$');
            }
            return param.test(value);
        }, "Invalid format.");

        /*
         * Lets you say "at least X inputs that match selector Y must be filled."
         *
         * The end result is that neither of these inputs:
         *
         *  <input class="productinfo" name="partnumber">
         *  <input class="productinfo" name="description">
         *
         *  ...will validate unless at least one of them is filled.
         *
         * partnumber:  {require_from_group: [1,".productinfo"]},
         * description: {require_from_group: [1,".productinfo"]}
         *
         */

        jQuery.validator.addMethod("require_from_group", function(value, element, options) {
            var validator = this;
            var selector = options[1];
            var validOrNot = $(selector, element.form).filter(function() {
                return validator.elementValue(this);
            }).length >= options[0];

            //// fix other fields not getting validated
            if(validator.currentElements.length == 1){
                if(!$(element).data('being_validated')) {
                    var fields = $(selector, element.form);
                    fields.data('being_validated', true);
                    var validResult = fields.valid();
                    fields.data('being_validated', false);
                    if(!validResult){
                        return validResult;
                    }
                }
            }
            return validOrNot;
        }, jQuery.format("Please fill at least {0} of these fields."));

        /*
         * Lets you say "either at least X inputs that match selector Y must be filled,
         * OR they must all be skipped (left blank)."
         *
         * The end result, is that none of these inputs:
         *
         *  <input class="productinfo" name="partnumber">
         *  <input class="productinfo" name="description">
         *  <input class="productinfo" name="color">
         *
         *  ...will validate unless either at least two of them are filled,
         *  OR none of them are.
         *
         * partnumber:  {skip_or_fill_minimum: [2,".productinfo"]},
         *  description: {skip_or_fill_minimum: [2,".productinfo"]},
         * color:       {skip_or_fill_minimum: [2,".productinfo"]}
         *
         */
        jQuery.validator.addMethod("skip_or_fill_minimum", function(value, element, options) {
            var validator = this,
                numberRequired = options[0],
                selector = options[1];
            var numberFilled = $(selector, element.form).filter(function() {
                return validator.elementValue(this);
            }).length;
            var valid = numberFilled >= numberRequired || numberFilled === 0;

            if(!$(element).data('being_validated')) {
                var fields = $(selector, element.form);
                fields.data('being_validated', true);
                fields.valid();
                fields.data('being_validated', false);
            }
            return valid;
        }, jQuery.format("Please either skip these fields or fill at least {0} of them."));




        jQuery.validator.addMethod("money", function(value, element) {
            return this.optional(element) || /^(([1-9]{1}\d{0,17})|([0]{1}))(\.(\d){1,2})?$/.test(value);
        }, "Please specify a valid money");



        jQuery.validator.addMethod("gt",function( value, element, param ){
            return this.optional(element) || value > param;
        },jQuery.format("Please enter a value greater than {0}."));
        jQuery.validator.addMethod("lt",function( value, element, param ){
            return this.optional(element) || value < param;
        },jQuery.format("Please enter a value less than {0}."));

        $.validator.addClassRules({
            date: {date: true},
            alphanumeric: { alphanumeric: true },
            lettersonly: { lettersonly: true },
            phone: { phone: true },
            postcode: {postcode: true},
            mobile: {mobile: true}
        });
        $.validator.setDefaults({success:"success"});
        $.validator.autoCreateRanges = true;
    }
    
    //地区验证
    jQuery.validator.addMethod("checkArea", function(value, element, options){
        var validator = this,areaSelector = options[0];
        if (!areaSelector.resetFromTxt()) {
            areaSelector.initTab([-1, -1, -1, -1], true);
            return false;
        }
        if (!areaSelector.isCodesComplete()) {
            return false;
        }
        return true;

    }, '请选择完整的地区信息!');

    //地区验证
    jQuery.validator.addMethod("checkAreaOrder", function(value, element, options){
        var validator = this,areaSelector = options[0];
        if (!areaSelector.resetFromTxt()) {
            return false;
        }
        if (!areaSelector.isCodesComplete()) {
            return false;
        }
        return true;

    }, '请选择完整的地区信息!');

    //地区验证
    jQuery.validator.addMethod("checkAreaOptional", function(value, element, options){

        if(!options)
        {
            return true;
        }
        var validator = this,areaSelector = options[0];
        if (!areaSelector.resetFromTxt()) {
            areaSelector.initTab([-1, -1, -1, -1], true);
            return false;
        }
        if (!areaSelector.isCodesComplete()) {
            return false;
        }
        return true;

    }, '请选择完整的地区信息!');

    //验证敏感词
    jQuery.validator.addMethod("words", function(value, element, options){
    	var flag=true;
    	var url= ZXB.Config.basePath + "/wordfiltration";
//    	var data={str:value};
    	$.ajax({
    			type : "post",
    	        async : false,
    	        data:{str:value},
    	        url : url,
    	        dataType : "json",
    	        success :function(result){
    	        	flag=result;
    	        }
    	});
    	return flag;
    }, '该内容存在敏感词!');


    jQuery.validator.addMethod("notblank", function(value, element) {
        return value.length > 0 ? $.trim(value).length > 0 : true;
    }, "不允许输入空白字符串");
})(jQuery);


(function($){
    // jQuery validate
    $.extend($.validator.messages, {
        required: "必填字段",
        remote: "请修正该字段",
        email: "请输入正确格式的电子邮件",
        url: "请输入合法的网址",
        date: "请输入合法的日期",
        dateISO: "请输入合法的日期 (ISO).",
        number: "请输入合法的数字",
        digits: "只能输入整数",
        creditcard: "请输入合法的信用卡号",
        equalTo: "请再次输入相同的值",
        accept: "请输入拥有合法后缀名的字符串",
        maxlength: $.validator.format("长度最多是 {0} 的字符串"),
        minlength: $.validator.format("长度最少是 {0} 的字符串"),
        rangelength: $.validator.format("长度介于 {0} 和 {1} 之间的字符串"),
        range: $.validator.format("请输入一个介于 {0} 和 {1} 之间的值"),
        max: $.validator.format("请输入一个最大为 {0} 的值"),
        min: $.validator.format("请输入一个最小为 {0} 的值"),

        alphanumeric: "字母、数字、下划线",
        lettersonly: "必须是字母",
        phone: "数字、空格、括号",
        mobile: "请输入手机号码",
        nowhitespace : "不允许输入空白字符串",
        money:"请输入合法的金额",
        time:"请输入00:00-23:59之间的时间",
        time12:"",
        gt:$.validator.format("请输入一个大于 {0} 的值"),
        lt:$.validator.format("请输入一个小于 {0} 的值")

    });
})(jQuery);