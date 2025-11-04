package project.simple_commerce.item.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import project.simple_commerce.item.dto.ItemResponseDto;
import project.simple_commerce.item.dto.create.CreateItemRequest;
import project.simple_commerce.item.dto.create.CreateItemResponse;
import project.simple_commerce.item.dto.update.UpdateItemRequest;
import project.simple_commerce.item.dto.update.UpdateItemResponse;
import project.simple_commerce.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getItems(){
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItem(@PathVariable Long id){
        return itemService.findById(id);
    }

    @PostMapping
    public CreateItemResponse create(@Valid @RequestBody CreateItemRequest createItemRequest){
        log.info("create item");
        return itemService.create(createItemRequest);
    }

    @PutMapping("/{id}")
    public UpdateItemResponse update(@PathVariable Long id,
                                     @RequestBody UpdateItemRequest updateItemRequest){
        return itemService.update(id, updateItemRequest);
    }

}

