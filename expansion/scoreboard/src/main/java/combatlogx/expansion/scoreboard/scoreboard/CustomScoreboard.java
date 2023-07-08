package combatlogx.expansion.scoreboard.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.github.sirblobman.api.language.ComponentHelper;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.scoreboard.ScoreboardHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.TextReplacementConfig;

import combatlogx.expansion.scoreboard.ScoreboardExpansion;

public final class CustomScoreboard {
    private final ScoreboardExpansion expansion;
    private final List<CustomLine> customLineList;
    private final Scoreboard scoreboard;
    private final Player player;
    private Objective objective;

    public CustomScoreboard(ScoreboardExpansion expansion, Player player) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.player = Validate.notNull(player, "player must not be null!");

        ScoreboardManager bukkitScoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = bukkitScoreboardManager.getNewScoreboard();
        this.customLineList = new ArrayList<>();

        createObjective();
        initializeScoreboard();
    }

    private ScoreboardExpansion getExpansion() {
        return this.expansion;
    }

    private ICombatLogX getCombatLogX() {
        ScoreboardExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    public Player getPlayer() {
        return this.player;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public Objective getObjective() {
        return this.objective;
    }

    public void enableScoreboard() {
        Player player = getPlayer();
        Scoreboard scoreboard = getScoreboard();
        player.setScoreboard(scoreboard);
    }

    public void disableScoreboard() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();

        Player player = getPlayer();
        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard() {
        updateTitle();

        List<Component> lineList = getLines();
        int lineListSize = lineList.size();

        for (int line = 16; line > 0; line--) {
            int index = (16 - line);
            if (index >= lineListSize) {
                removeLine(line);
                continue;
            }

            Component value = lineList.get(index);
            setLine(line, value);
        }
    }

    private void createObjective() {
        ICombatLogX plugin = getCombatLogX();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ScoreboardHandler scoreboardHandler = multiVersionHandler.getScoreboardHandler();

        Scoreboard scoreboard = getScoreboard();
        this.objective = scoreboardHandler.createObjective(scoreboard, "combatlogx", "dummy",
                "Default Title");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        updateTitle();
    }

    private void initializeScoreboard() {
        Scoreboard scoreboard = getScoreboard();
        ChatColor[] chatColorArray = {
                ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED,
                ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE,
                ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW,
                ChatColor.WHITE
        };
        int chatColorArrayLength = chatColorArray.length;

        for (int i = 0; i < chatColorArrayLength; i++) {
            ChatColor chatColor = chatColorArray[i];
            String chatColorString = chatColor.toString();

            String teamName = ("line" + i);
            Team team = scoreboard.registerNewTeam(teamName);
            team.addEntry(chatColorString);

            CustomLine customLine = new CustomLine(chatColor, team, i + 1);
            this.customLineList.add(customLine);
        }
    }

    private CustomLine getLine(int line) {
        return this.customLineList.get(line - 1);
    }

    private void setLine(int line, Component value) {
        CustomLine customLine = getLine(line);
        Validate.notNull(customLine, "Could not find scoreboard line '" + line + "'.");

        ChatColor chatColor = customLine.getColor();
        String chatColorString = chatColor.toString();

        Objective objective = getObjective();
        Score score = objective.getScore(chatColorString);
        score.setScore(line);

        ScoreboardExpansion expansion = getExpansion();
        if (expansion.isPaperScoreboard()) {
            setLinePaper(line, value);
        } else {
            String valueString = ComponentHelper.toLegacy(value);
            setLineSpigot(line, valueString);
        }
    }

    private void setLinePaper(int line, Component prefix) {
        CustomLine customLine = getLine(line);
        Validate.notNull(customLine, "Could not find scoreboard line '" + line + "'.");
        PaperScoreboard.setLine(customLine, prefix);
    }

    @SuppressWarnings("deprecation")
    private void setLineSpigot(int line, String value) {
        CustomLine customLine = getLine(line);
        Validate.notNull(customLine, "Could not find scoreboard line '" + line + "'.");

        int lengthLimit = getLineLengthLimit();
        int valueLength = value.length();
        if (valueLength <= lengthLimit) {
            Team team = customLine.getTeam();
            team.setPrefix(value);
            team.setSuffix("");
            return;
        }

        String partOne = cut(value, lengthLimit);
        String partTwo = value.substring(lengthLimit);

        String partOneFinalColors = ChatColor.getLastColors(partOne);
        partTwo = cut((partOneFinalColors + partTwo), lengthLimit);

        Team team = customLine.getTeam();
        team.setPrefix(partOne);
        team.setSuffix(partTwo);
    }

    private String cut(String original, int length) {
        int originalLength = original.length();
        if (originalLength <= length) {
            return original;
        }

        return original.substring(0, length);
    }

    private void removeLine(int line) {
        CustomLine customLine = getLine(line);
        Validate.notNull(customLine, "Could not find scoreboard line '" + line + "'.");

        ChatColor chatColor = customLine.getColor();
        String chatColorString = chatColor.toString();
        Scoreboard scoreboard = getScoreboard();
        scoreboard.resetScores(chatColorString);
    }

    private int getLineLengthLimit() {
        int minorVersion = VersionUtility.getMinorVersion();
        return (minorVersion > 12 ? 64 : 16);
    }

    private List<Component> getLines() {
        Player player = getPlayer();
        LanguageManager languageManager = getLanguageManager();
        List<Component> preMessageList = languageManager.getMessageList(player, "expansion.scoreboard.lines");
        List<Component> finalMessageList = new ArrayList<>();

        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        IPlaceholderManager placeholderManager = combatLogX.getPlaceholderManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        TextReplacementConfig replacementConfig = null;

        if (tagInformation != null) {
            List<Entity> enemyList = tagInformation.getEnemies();
            Pattern placeholderPattern = Pattern.compile("\\{(\\S+)}");
            TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
            builder.match(placeholderPattern);
            builder.replacement((matchResult, builderCopy) -> {
                String placeholder = matchResult.group(1);
                Component replacement = placeholderManager.getPlaceholderReplacementComponent(player,
                        enemyList, placeholder);
                return (replacement == null ? Component.text(placeholder) : replacement);
            });

            replacementConfig = builder.build();
        }

        for (Component preMessage : preMessageList) {
            Component finalMessage = preMessage;
            if (replacementConfig != null) {
                finalMessage = finalMessage.replaceText(replacementConfig);
            }

            finalMessageList.add(finalMessage);
        }

        return finalMessageList;
    }

    private Component getTitle() {
        Player player = getPlayer();
        LanguageManager languageManager = getLanguageManager();

        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        IPlaceholderManager placeholderManager = combatLogX.getPlaceholderManager();
        Component preMessage = languageManager.getMessage(player, "expansion.scoreboard.title");

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation != null) {
            List<Entity> enemyList = tagInformation.getEnemies();
            Pattern placeholderPattern = Pattern.compile("\\{(\\S+)}");
            TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
            builder.match(placeholderPattern);
            builder.replacement((matchResult, builderCopy) -> {
                String placeholder = matchResult.group(1);
                String replacement = placeholderManager.getPlaceholderReplacement(player, enemyList, placeholder);
                return Component.text(replacement == null ? placeholder : replacement);
            });

            TextReplacementConfig replacement = builder.build();
            preMessage = preMessage.replaceText(replacement);
        }

        return preMessage;
    }

    private void updateTitle() {
        Component title = getTitle();
        ScoreboardExpansion expansion = getExpansion();

        if (expansion.isPaperScoreboard()) {
            updateTitlePaper(title);
        } else {
            String spigotTitle = ComponentHelper.toLegacy(title);
            updateTitleSpigot(spigotTitle);
        }
    }

    private void updateTitlePaper(Component title) {
        Objective objective = getObjective();
        PaperScoreboard.setTitle(objective, title);
    }

    @SuppressWarnings("deprecation")
    private void updateTitleSpigot(String title) {
        Objective objective = getObjective();
        objective.setDisplayName(title);
    }
}
