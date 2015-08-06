package cz.fi.muni.pa165.rest;

import cz.fi.muni.pa165.PersistenceSampleApplicationContext;
import cz.fi.muni.pa165.SpringMVCConfig;
import cz.fi.muni.pa165.dto.CategoryDTO;
import cz.fi.muni.pa165.facade.CategoryFacade;
import cz.fi.muni.pa165.rest.controllers.CategoriesController;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@WebAppConfiguration
@ContextConfiguration(classes = {PersistenceSampleApplicationContext.class, SpringMVCConfig.class})
public class CategoriesControllerTest {

    @Mock
    private CategoryFacade categoryFacade;

    @Autowired
    @InjectMocks
    private CategoriesController categoriesController;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(categoriesController).setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    public void getAllCategories() throws Exception {

        doReturn(Collections.unmodifiableList(this.createCategories())).when(categoryFacade).getAllCategories();

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[?(@.id==1)].name").value("Electronics"))
                .andExpect(jsonPath("$.[?(@.id==2)].name").value("Home Appliances"));

    }

    @Test
    public void getValidProduct() throws Exception {

        List<CategoryDTO> products = this.createCategories();

        doReturn(products.get(0)).when(categoryFacade).getCategoryById(1l);
        doReturn(products.get(1)).when(categoryFacade).getCategoryById(2l);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("Electronics"));

        mockMvc.perform(get("/categories/2"))
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value("Home Appliances"));

    }

    @Test
    public void getInvalidProduct() throws Exception {
        doReturn(null).when(categoryFacade).getCategoryById(1l);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().is4xxClientError());

    }

    private List<CategoryDTO> createCategories() {
        CategoryDTO catOne = new CategoryDTO();
        catOne.setId(1l);
        catOne.setName("Electronics");

        CategoryDTO catTwo = new CategoryDTO();
        catTwo.setId(2l);
        catTwo.setName("Home Appliances");

        return Arrays.asList(catOne, catTwo);
    }
}
