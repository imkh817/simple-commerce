package project.simple_commerce.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.simple_commerce.item.dto.ItemResponseDto;
import project.simple_commerce.item.dto.create.CreateItemRequest;
import project.simple_commerce.item.dto.create.CreateItemResponse;
import project.simple_commerce.item.dto.update.UpdateItemRequest;
import project.simple_commerce.item.dto.update.UpdateItemResponse;
import project.simple_commerce.item.entity.Item;
import project.simple_commerce.item.exception.NotFoundItemException;
import project.simple_commerce.item.repository.ItemRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemResponseDto> findAll(){
        return itemRepository.findAll().stream()
                .map(ItemResponseDto::from)
                .toList();
    }

    public ItemResponseDto findById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundItemException("Item not found with id: " + id));

        return ItemResponseDto.from(item);
    }

    @Transactional
    public CreateItemResponse create(CreateItemRequest createItemRequest) {
        Item item = Item.builder()
                .itemName(createItemRequest.getItemName())
                .price(createItemRequest.getPrice())
                .stockQuantity(createItemRequest.getStockQuantity())
                .build();

        Item savedItem = itemRepository.save(item);
        return CreateItemResponse.from(savedItem);
    }

    @Transactional
    public UpdateItemResponse update(Long id, UpdateItemRequest updateItemRequest) {
        Item findItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundItemException("Item not found with id: " + id));

        findItem.updateInfo(updateItemRequest);

        return UpdateItemResponse.from(findItem);
    }
}
