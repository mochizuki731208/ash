// function AddressSearch(city_name, page_size, search_complete) {
function AddressSearch(city_name, page_size, search_complete) {
    var that = this;
    that.data = {};
    that.search_complete = search_complete || function(){};
    that._page_index = 0;

    var on_search_complete = function(results) {
        if (that._bMap.getStatus() == BMAP_STATUS_SUCCESS) {
            // 判断状态是否正确
            var search_data = {
                keyword     : results.keyword,
                data        : [],
                page_index  : results.getPageIndex(),
                page_num    : results.getNumPages()
            };
            for (var i = 0; i < results.getCurrentNumPois(); i++) {
                var poi = results.getPoi(i);
                search_data.data.push({
                    title   : poi.title,
                    address : (poi.address || ""),
                    lat     : poi.point.lat,
                    lng     : poi.point.lng,
                    city    : poi.city,
                    province: poi.province
                });
            }
            that.set_page_index(results.getPageIndex());
            that.set_cached_data(results.keyword, results.getPageIndex(), search_data);
            that.search_complete(search_data);
        } else {
            that.search_complete(false);
        }
    };
    that._bMap = new BMap.LocalSearch(city_name, {
        onSearchComplete: on_search_complete
    });
    that._bMap.setLocation(city_name);
    that._bMap.setPageCapacity(page_size);
}

AddressSearch.prototype.get_cached_data = function(keyword, page_index) {
    return this.data[keyword+"::"+page_index];
};
AddressSearch.prototype.set_cached_data = function(keyword, page_index, search_data) {
    this.data[keyword+"::"+page_index] = search_data;
};

AddressSearch.prototype.search = function(query_str) {
    var that = this;

    that.keyword = query_str;
    that._page_index = 0;
    var search_data = that.get_cached_data(that.keyword, that._page_index);
    if (search_data) {
        that.search_complete(search_data);
    } else {
        that._bMap.search(that.keyword, {forceLocal: false});
    }
};

AddressSearch.prototype.goto_pre_page = function() {
    var that = this;

    var current_page_index = that.get_page_index() - 1;
    if (current_page_index < 0) {
        current_page_index = 0;
    }
    that.goto_page(current_page_index);
};

AddressSearch.prototype.goto_next_page = function() {
    var that = this;

    var current_page_index = that.get_page_index() + 1;
    if (current_page_index < that.get_page_num()) {
        that.goto_page(current_page_index);
    } else {
        that.search_complete(false);
    }
};

AddressSearch.prototype.goto_page = function(page_index) {
    var that = this;

    var search_data = that.get_cached_data(that.keyword, page_index);
    if (search_data) {
        that.set_page_index(page_index);
        that.search_complete(search_data);
    } else {
        that._bMap.gotoPage(page_index);
    }
};

AddressSearch.prototype.get_city_list = function() {
    return this._bMap.getResults().getCityList();
};
AddressSearch.prototype.get_page_size = function() {
    return this._bMap.getResults().getCurrentNumPois();
};
AddressSearch.prototype.get_page_num = function() {
    return this._bMap.getResults().getNumPages();
};
AddressSearch.prototype.get_position_size = function() {
    return this._bMap.getResults().getNumPois();
};
AddressSearch.prototype.get_page_index = function() {
    return this._page_index;
};
AddressSearch.prototype.set_page_index = function(page_index) {
    this._page_index = page_index;
};
