package org.glocol.mallapi.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.glocol.mallapi.dto.PageRequestDTO;
import org.glocol.mallapi.dto.PageResponseDTO;
import org.glocol.mallapi.dto.ProductDTO;
import org.glocol.mallapi.service.ProductService;
import org.glocol.mallapi.util.CustomFileUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final CustomFileUtil fileUtil;

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName) {
        return fileUtil.getFile(fileName);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        log.info("list......................" + pageRequestDTO);

        return productService.getList(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, Long> register(ProductDTO productDTO) {
        log.info("register: " + productDTO);
        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadFileNames = fileUtil.saveFiles(files);
        productDTO.setUploadFileNames(uploadFileNames);
        log.info(uploadFileNames);

        Long pno = productService.register(productDTO);

        return Map.of("RESULT", pno);
    }

    @GetMapping("/{tno}")
    public ProductDTO read(@PathVariable(name = "tno") Long tno) {
        return productService.get(tno);
    }

    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable(name = "pno") Long pno, ProductDTO productDTO) {
        productDTO.setPno(pno);
        ProductDTO oldProductDTO = productService.get(pno);
        List<String> oldFileNames = oldProductDTO.getUploadFileNames();
        List<MultipartFile> files = productDTO.getFiles();
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);
        List<String> uploadedFileNames = productDTO.getUploadFileNames();

        if (currentUploadFileNames != null && currentUploadFileNames.size() > 0) {
            uploadedFileNames.addAll(currentUploadFileNames);
        }

        productService.modify(productDTO);

        if (oldFileNames != null && oldFileNames.size() > 0) {
            List<String> removeFiles = oldFileNames.stream()
                    .filter(fileName -> uploadedFileNames.indexOf(fileName) == -1)
                    .collect(Collectors.toList());
            fileUtil.deleteFiles(removeFiles);
        }

        return Map.of("RESULT","SUCCESS");
    }

    @DeleteMapping("/{pno}")
    public Map<String, String> remove(@PathVariable(name ="pno") Long pno) {
        ProductDTO oldProductDTO = productService.get(pno);
        List<String> oldFileNames = oldProductDTO.getUploadFileNames();
        productService.remove(pno);
        fileUtil.deleteFiles(oldFileNames);
        return Map.of("RESULT", "SUCCESS");
    }
}
