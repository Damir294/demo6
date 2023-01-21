package Controllers;




import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@RestController
@RequestMapping("/files")
@Tag(name = "Передача файлов по HTTP", description = "API для работы с файлами")
public class FilesController {
    @Value("${path.to.data.file}")
    private String dataFilepath;
    @Value("${name.of.recipes.data.file}")
    private String recipeDataFileName;

    @Value("${name.of.ingredients.data.file}")
    private String ingredientDataFileName;

    private final FileService fileService;

    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }


    @Operation(
            summary = "Скачать все рецепты в виде json-файла"
    )
    @GetMapping(value = "/recipes/download")
    public ResponseEntity<InputStreamResource> downloadRecipesFile() throws IOException {
        File file = new File(dataFilepath + "/" + recipeDataFileName);
        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + recipeDataFileName)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Принимает json-файл с рецептами и заменяет сохраненный на жестком диске файл на новый"
    )
    @PostMapping(value = "/recipes/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadRecipesFile(@RequestParam MultipartFile file) {
        try {
            fileService.uploadFile(file, recipeDataFileName);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Operation(
            summary = "Принимает json-файл с ингредиентами и заменяет сохраненный на жестком диске файл на новый"
    )
    @PostMapping(value = "/ingredients/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadIngredientsFile(@RequestParam MultipartFile file) {
        try {
            fileService.uploadFile(file, ingredientDataFileName);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}