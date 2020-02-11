package us.flower.dayary.service.moim.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import us.flower.dayary.common.FileManager;
import us.flower.dayary.domain.UploadFile;
import us.flower.dayary.repository.community.BoardFileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MoimImageServiceImpl implements MoimImageService {

    private final Path rootLocation;

    public MoimImageServiceImpl(String moimImagePath){
        this.rootLocation = Paths.get(moimImagePath);
    }

    @Autowired
    BoardFileRepository fileRepository;

    @Override
    public Stream<Long> loadAll() {
        List<UploadFile> files = fileRepository.findAll();
        return files.stream().map(file -> file.getId());
    }

    @Override
    public UploadFile load(long fileId) {
        return fileRepository.findById(fileId);
    }

    @Override
    public Resource loadAsResource(String fileName) throws Exception {
        try{
            if(fileName.toCharArray()[0]=='/'){
                fileName = fileName.substring(1);
            }

            Path file = loadPath(fileName);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            } else {
                throw new Exception("Could not read file: " + fileName);
            }
        }catch(Exception e){
            throw new Exception("Could not read file:" + fileName);
        }
    }

    @Override
    public Path loadPath(String fileName) {
        return rootLocation.resolve(fileName);
    }

    @Override
    public UploadFile store(MultipartFile file) throws Exception {
        try{
            if(file.isEmpty()){
                throw new Exception("Failed to store empty file " + file.getOriginalFilename());
            }
            String saveFileName = FileManager.fileSave(rootLocation.toString(), file);

            if(saveFileName.toCharArray()[0] == '/'){
                saveFileName = saveFileName.substring(1);
            }

            Resource resource = loadAsResource(saveFileName);

            UploadFile saveFile = new UploadFile();
            saveFile.setSaveFileName(saveFileName);
            saveFile.setFileName(file.getOriginalFilename());
            saveFile.setContentType(file.getContentType());
            saveFile.setFilePath(rootLocation.toString() + File.separator + saveFileName);
            saveFile.setSize(resource.contentLength());
            saveFile = fileRepository.save(saveFile);

            return saveFile;
        }catch(IOException e){
            throw new Exception("Failed to store file" + file.getOriginalFilename(), e);
        }
    }
}
