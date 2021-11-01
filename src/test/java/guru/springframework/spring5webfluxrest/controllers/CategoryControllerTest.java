package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class CategoryControllerTest {

  WebTestClient webTestClient;
  CategoryRepository categoryRepository;

  CategoryController testling;

  @Before
  public void setUp() throws Exception {
    categoryRepository = Mockito.mock(CategoryRepository.class);
    testling = new CategoryController(categoryRepository);

    webTestClient = WebTestClient.bindToController(testling).build();
  }

  @Test
  public void testList() {
    given(categoryRepository.findAll()).willReturn(
      Flux.just(Category.builder().description("nuts").build(), Category.builder().description("egg").build()));

    webTestClient.get().uri("/api/v1/categories")
      .exchange()
      .expectBodyList(Category.class)
      .hasSize(2);
  }

  @Test
  public void testGetByID() {
    Category findingCategorie = Category.builder().id("234").description("egg").build();
    given(categoryRepository.findById(anyString())).willReturn(Mono.just(findingCategorie));

    webTestClient.get().uri("/api/v1/categories/234")
      .exchange()
      .expectBody(Category.class)
      .isEqualTo(findingCategorie);
  }

  @Test
  public void testCreate(){
    given(categoryRepository.saveAll(any(Publisher.class))).willReturn(Flux.just(Category.builder().build()));

    Mono<Category> categoryMono = Mono.just(Category.builder().description("Some cat").build());

    webTestClient.post().uri("/api/v1/categories").body(categoryMono, Category.class)
      .exchange()
      .expectStatus()
      .isCreated();
  }

  @Test
  public void testUpdate(){
    given(categoryRepository.save(any(Category.class))).willReturn(Mono.just(Category.builder().build()));

    Mono<Category> updateMono = Mono.just(Category.builder().description("Some cat").build());

    webTestClient.put().uri("/api/v1/categories/12115")
      .body(updateMono, Category.class)
      .exchange()
      .expectStatus()
      .isOk();
  }

  @Test
  public void testPatch(){
    given(categoryRepository.findById(anyString())).willReturn(Mono.just(Category.builder().build()));
    given(categoryRepository.save(any(Category.class))).willReturn(Mono.just(Category.builder().build()));

    Mono<Category> patchMono = Mono.just(Category.builder().description("Some cat").build());

    webTestClient.patch().uri("/api/v1/categories/12115")
      .body(patchMono, Category.class)
      .exchange()
      .expectStatus()
      .isOk();

    verify(categoryRepository).save(any());
  }

  @Test
  public void testPatchNoChanges(){
    given(categoryRepository.findById(anyString())).willReturn(Mono.just(Category.builder().build()));
    given(categoryRepository.save(any(Category.class))).willReturn(Mono.just(Category.builder().build()));

    Mono<Category> patchMono = Mono.just(Category.builder().build());

    webTestClient.patch().uri("/api/v1/categories/12115")
      .body(patchMono, Category.class)
      .exchange()
      .expectStatus()
      .isOk();

    verify(categoryRepository, never()).save(any());
  }
}