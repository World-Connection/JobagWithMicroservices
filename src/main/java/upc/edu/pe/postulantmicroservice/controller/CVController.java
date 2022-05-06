package upc.edu.pe.postulantmicroservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import upc.edu.pe.postulantmicroservice.entity.CV;
import upc.edu.pe.postulantmicroservice.services.CVService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value ="/cv")
public class CVController {
    @Autowired
    private CVService cvService;

    @GetMapping
    public ResponseEntity<List<CV>> listCV(){
        List<CV> cvs = new ArrayList<>();
        cvs = cvService.findAllCV();
        if(cvs.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cvs);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CV> getCV(@PathVariable("id") Long id){
        CV cv = cvService.getCV(id);
        if(cv == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cv);
    }

    @PostMapping
    public ResponseEntity<CV> createCV(@Valid @RequestBody CV cv, BindingResult result){
        if(result.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, this.formatMessage(result));
        }
        CV cvCreate = cvService.createCV(cv);
        return ResponseEntity.status(HttpStatus.CREATED).body(cvCreate);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CV> updateCV(@PathVariable("id") Long id, @RequestBody CV cv){
        cv.setId(id);
        CV cvDB = cvService.updateCV(cv);
        if( cvDB == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cvDB);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<CV> deleteCV(@PathVariable("id") Long id){
        CV cvDelete = cvService.deleteCV(id);
        if(cvDelete == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cvDelete);
    }

    private String formatMessage( BindingResult result){
        List<Map<String,String>> errors = result.getFieldErrors().stream()
                .map(err ->{
                    Map<String,String>  error =  new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;

                }).collect(Collectors.toList());
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code("01")
                .messages(errors).build();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString="";
        try {
            jsonString = mapper.writeValueAsString(errorMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
