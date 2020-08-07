package ru.homyakin.gwent.service.action;

import io.vavr.control.Either;
import java.util.List;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.FactionCardsData;
import ru.homyakin.gwent.models.exceptions.EitherError;
import ru.homyakin.gwent.models.exceptions.ParsingError;
import ru.homyakin.gwent.models.exceptions.ProfileIsHidden;
import ru.homyakin.gwent.models.exceptions.ProfileNotFound;
import ru.homyakin.gwent.service.HttpService;
import java.lang.Exception;
import ru.homyakin.gwent.utils.GwentProfileUtils;

@Service
public class GwentCardsAction {
    private final static Logger logger = LoggerFactory.getLogger(GwentCardsAction.class);
    private final HttpService httpService;

    public GwentCardsAction(HttpService httpService) {
        this.httpService = httpService;
    }

    public Either<EitherError, String> getCards(String name) {
        var body = httpService.getHtmlBodyByUrl(String.format("https://www.playgwent.com/en/profile/%s", name));
        if (body.isLeft()) {
            return body;
        }
        try {
            var doc = Jsoup.parse(body.get());
            if (GwentProfileUtils.isHidden(doc)) {
                return Either.left(new ProfileIsHidden(name));
            }
            if (GwentProfileUtils.isNotFound(doc)) {
                return Either.left(new ProfileNotFound(name));
            }
            var gwentCards = GwentProfileUtils.getCardsData(doc);
            return Either.right(GwentProfileUtils.getName(doc) + "\n" + convertAllFactionCardsToString(gwentCards));
        } catch (Exception e) {
            logger.error("Unexpected error during parsing", e);
            return Either.left(new ParsingError());
        }
    }

    private String convertAllFactionCardsToString(List<FactionCardsData> list) {
        StringBuilder response = new StringBuilder();
        for (var factionCards : list) {
            response.append(factionCards.toString()).append("\n");
        }
        return response.toString();
    }
}
