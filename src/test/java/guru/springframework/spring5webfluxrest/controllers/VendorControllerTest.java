package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
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
public class VendorControllerTest {

  WebTestClient webTestClient;

  VendorRepository vendorRepository;

  VendorController testling;

  @Before
  public void setUp() {
    vendorRepository = Mockito.mock(VendorRepository.class);

    testling = new VendorController(vendorRepository);

    webTestClient = WebTestClient.bindToController(testling).build();
  }

  @Test
  public void testListAllVendors() {
    given(vendorRepository.findAll()).willReturn(
      Flux.just(Vendor.builder().firstname("Hans").lastname("Schmit").build(),
        Vendor.builder().firstname("Test").lastname("User").build()));

    webTestClient.get().uri("/api/v1/vendors")
      .exchange()
      .expectBodyList(Vendor.class)
      .hasSize(2);
  }

  @Test
  public void testFindVendorById(){
    Vendor findingVendor = Vendor.builder().id("1234").lastname("Hampel").firstname("Mann").build();
    given(vendorRepository.findById(anyString())).willReturn(Mono.just(findingVendor));

    webTestClient.get().uri("/api/v1/vendors/1234")
      .exchange()
      .expectBody(Vendor.class)
      .isEqualTo(findingVendor);
  }

  @Test
  public void testCreate(){
    given(vendorRepository.saveAll(any(Publisher.class))).willReturn(Flux.just(Category.builder().build()));

    Mono<Vendor> vendorMono = Mono.just(Vendor.builder().lastname("Some cat").build());

    webTestClient.post().uri("/api/v1/vendors").body(vendorMono, Vendor.class)
      .exchange()
      .expectStatus()
      .isCreated();
  }

  @Test
  public void testUpdate(){
    given(vendorRepository.save(any(Vendor.class))).willReturn(Mono.just(Vendor.builder().build()));

    Mono<Vendor> updateMono = Mono.just(Vendor.builder().lastname("Some cat").build());

    webTestClient.put().uri("/api/v1/vendors/12115")
      .body(updateMono, Vendor.class)
      .exchange()
      .expectStatus()
      .isOk();
  }

  @Test
  public void testPatch(){
    given(vendorRepository.findById(anyString())).willReturn(Mono.just(Vendor.builder().build()));
    given(vendorRepository.save(any(Vendor.class))).willReturn(Mono.just(Vendor.builder().build()));

    Mono<Vendor> patchMono = Mono.just(Vendor.builder().lastname("Some cat").build());

    webTestClient.patch().uri("/api/v1/vendors/12115")
      .body(patchMono, Vendor.class)
      .exchange()
      .expectStatus()
      .isOk();

    verify(vendorRepository).save(any());
  }

  @Test
  public void testPatchNoChanges(){
    given(vendorRepository.findById(anyString())).willReturn(Mono.just(Vendor.builder().build()));
    given(vendorRepository.save(any(Vendor.class))).willReturn(Mono.just(Vendor.builder().build()));

    Mono<Vendor> patchMono = Mono.just(Vendor.builder().build());

    webTestClient.patch().uri("/api/v1/vendors/12115")
      .body(patchMono, Vendor.class)
      .exchange()
      .expectStatus()
      .isOk();

    verify(vendorRepository, never()).save(any());
  }
}