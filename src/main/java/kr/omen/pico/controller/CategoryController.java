package kr.omen.pico.controller;

import kr.omen.pico.service.domain.dto.CategoryDTO;
import kr.omen.pico.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category/getAllCategory")
    public List<CategoryDTO> getAllCategory(){
        List<CategoryDTO> list = categoryService.getAllCategory();
        return list;
    }

}
