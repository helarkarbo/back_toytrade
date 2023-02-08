package ee.valiit.back_toytrade.trade;

import ee.valiit.back_toytrade.domain.category.Category;
import ee.valiit.back_toytrade.domain.category.CategoryService;
import ee.valiit.back_toytrade.domain.city.City;
import ee.valiit.back_toytrade.domain.city.CityService;
import ee.valiit.back_toytrade.domain.condition.Condition;
import ee.valiit.back_toytrade.domain.condition.ConditionService;
import ee.valiit.back_toytrade.domain.picture.PictureRepository;
import ee.valiit.back_toytrade.domain.picture.PictureService;
import ee.valiit.back_toytrade.domain.toy.Toy;
import ee.valiit.back_toytrade.domain.user.User;
import ee.valiit.back_toytrade.domain.user.UserService;
import ee.valiit.back_toytrade.domain.user.role.Role;
import ee.valiit.back_toytrade.domain.user.role.RoleService;
import ee.valiit.back_toytrade.trade.dto.ToyDto;
import ee.valiit.back_toytrade.domain.toy.ToyMapper;
import ee.valiit.back_toytrade.domain.toy.ToyService;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TradeService {

    @Resource
    private ToyService toyService;

    @Resource
    private ToyMapper toyMapper;

    @Resource
    private UserService userService;

    @Resource
    private CityService cityService;

    @Resource
    private ConditionService conditionService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private PictureService pictureService;
    private final PictureRepository pictureRepository;

    public TradeService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }


    public List<ToyDto> getToysByCategory(Integer categoryId) {
        List<Toy> toys = toyService.findActiveListedToys(categoryId);
        List<ToyDto> toyDtos = toyMapper.toDtos(toys);

        return toyDtos;
    }


    public void addNewToy(ToyDto toyDto) {
        Toy toy = toyMapper.toEntity(toyDto);
        Optional<User> userById = userService.findUserById(toyDto.getUserId());
        toy.setUser(userById.get());
        Optional<City> cityById = cityService.findCityById(toyDto.getCityId());
        toy.setCity(cityById.get());
        Optional<Condition> conditionById = conditionService.findConditionById(toyDto.getConditionId());
        toy.setCondition(conditionById.get());
        Optional<Category> categoryById = categoryService.findCategoryById(toyDto.getCategoryId());
        toy.setCategory(categoryById.get());

        toyService.addNewToy(toy);
    }

    public void getToysByCategories(String categoryIds) {

    }
}
