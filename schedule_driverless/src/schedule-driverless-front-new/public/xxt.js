 function encode_xxt(reqpara, reqtime, searchQuery, searchBody) {
            var ptr_reqpara = _malloc(1024 * 50);
            var ptr_reqtime = _malloc(30);
            var ptr_searchQuery = (searchQuery ? _malloc(1024 * 20) : 0);
            var ptr_searchBody = (searchBody ? _malloc(1024 * 200) : 0);
            stringToUTF8(reqpara, ptr_reqpara, 1024 * 50);
            stringToUTF8(reqtime, ptr_reqtime, 1024 * 50);
            if (ptr_searchQuery != 0) stringToUTF8(searchQuery, ptr_searchQuery, 1024 * 50);
            if (ptr_searchBody != 0) stringToUTF8(searchBody, ptr_searchBody, 1024 * 50);
            var str = Pointer_stringify(_SHA_MD5(ptr_reqpara, ptr_reqtime, ptr_searchQuery, ptr_searchBody));
            _free(ptr_reqpara);
            _free(ptr_reqtime);
            if (ptr_searchQuery != 0) _free(ptr_searchQuery);
            if (ptr_searchBody != 0) _free(ptr_searchQuery);
            return str;
}