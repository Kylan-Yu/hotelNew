package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.vo.SystemDictItemVO;
import com.kylan.hotel.service.SystemManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final SystemManageService systemManageService;

    @GetMapping("/{dictCode}/items")
    public ApiResponse<List<SystemDictItemVO>> dictItems(@PathVariable("dictCode") String dictCode) {
        return ApiResponse.success(systemManageService.dictItems(dictCode, true));
    }
}
