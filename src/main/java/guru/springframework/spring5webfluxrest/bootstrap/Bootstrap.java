package guru.springframework.spring5webfluxrest.bootstrap;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

  private final CategoryRepository categoryRepository;
  private final VendorRepository vendorRepository;

  public Bootstrap(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
    this.categoryRepository = categoryRepository;
    this.vendorRepository = vendorRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (categoryRepository.count().block() == 0) {
      // load data
      System.out.println("######### LOAD Category data");
      categoryRepository.save(Category.builder().description("fruit").build()).block();
      categoryRepository.save(Category.builder().description("nuts").build()).block();
      categoryRepository.save(Category.builder().description("breads").build()).block();
      categoryRepository.save(Category.builder().description("meats").build()).block();

      System.out.println("LOADED Categories " + categoryRepository.count().block());
    }

    if(vendorRepository.count().block() == 0){
      // load data
      System.out.println("######### LOAD Vendor data");
      vendorRepository.save(Vendor.builder().firstname("Helmut").lastname("Franz").build()).block();
      vendorRepository.save(Vendor.builder().firstname("Kohl").lastname("Kopf").build()).block();
      vendorRepository.save(Vendor.builder().firstname("Mira").lastname("Bellenbaum").build()).block();
      vendorRepository.save(Vendor.builder().firstname("Fritz").lastname("Klein").build()).block();
      vendorRepository.save(Vendor.builder().firstname("Helga").lastname("Neu").build()).block();

      System.out.println("LOADED Vendor " + vendorRepository.count().block());
    }
  }
}
