package com.github.thelampgod.snow.config;

import com.github.thelampgod.snow.ConfigManager;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Setting<T> {
    public Setting(ConfigManager owner, T defaultValue, Function<String, T> load, Function<T, String> save) {
        this.inMod = owner;// do we need to enforce default value?
        this.owner = owner;
        defaultValue(defaultValue);
        this.load = load;
        this.save = save;

    }

    private final ConfigManager inMod;// to be able to have same setting names in different mods
    private Object owner;
    private final Function<String, T> load;
    private final Function<T, String> save;
    private String name;
    private String description = null; // we have no description if its not set...
    private Supplier<T> defaultValue = null;// ideally its a function that takes in a context?
    private ValueIs<T> valueIs = null;
    public VizSettings<T> vizSettings = new VizSettings<>();
    public Consumer<ChangeInfo<T>> onChange = null;

    public String getDefaultAsString() {
        return save.apply(defaultValue.get());
    }

    public String getForSave() {
        return save.apply(get());
    }

    public static class ChangeInfo<T> {
        boolean cancel;

        public Source source;
        public ValueIs<T> was;
        public ValueIs<T> willBe;


        public ChangeInfo(Source source, ValueIs<T> valueIs, ValueIs<T> what) {
            this.source = source;
            was = valueIs;
            willBe = what;
        }

        public void cancel() {
            cancel = true;
        }

        public enum Source {
            GUI,
            COMMAND,
            MOD
        }
    }

    public Setting<T> owner(Object settingIsInThere) {
        owner = settingIsInThere;
        return this;
    }

    public Setting<T> name(String name) {
        this.name = name;
        return this;
    }

    public Setting<T> desc(String desc) {
        this.description = desc;
        return this;
    }


    public Setting<T> defaultValue(T v) {
        this.defaultValue = () -> v;
        return this;
    }

    public String getName() {
        if (name == null) {
            // find field where we are saved in to get name... by going thru all the fields of owner with reflection
            Class<?> clazz = owner.getClass();
            while (clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    //if (field.getType() == this.getClass()) {
                    field.setAccessible(true);
                    try {
                        if (field.get(owner) == this) {
                            name = field.getName();
                            return name;
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);// yea I mean... this shouldnt happen, right?
                    }
                    //}
                }
                clazz = clazz.getSuperclass();
            }

            name = "unknown";// alert or something bc we couldnt find the name somehow...
        }

        return name;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public T get() {
        if (valueIs == null) {
            String value = inMod.giveSettingReference(getName(), this);// this needs to be reworked anyway when saving will be improved...
            // ideally we would know now if the setting is actual custom or reference to the default value...
            valueIs = new ValueIsCustom<>(load.apply(value));
        }
        return valueIs.get(this);// should also give it a context variable?
    }

    public void set(T value) {
        set(ChangeInfo.Source.MOD, new ValueIsCustom<>(value));
    }

    public void set(ChangeInfo.Source source, ValueIs<T> what) {
        if (onChange != null) {
            ChangeInfo<T> changeInfo = new ChangeInfo<>(source, valueIs, what);
            onChange.accept(changeInfo);
            if (changeInfo.cancel) return;// alert or something? not sure when this would be useful anyway?
        }
        valueIs = what;
    }

    public Setting<T> sliderRange(T min, T max) {
        vizSettings.min = min;
        vizSettings.max = max;
        return this;
    }

    private static class DefaultValues<T> {
        T defaultDefault;
        // maybe also other default values depending on server/player/dimension... anything...
    }

    public interface ValueIs<T> {
        T get(Setting<T> setting);
    }

    public static class ValueIsDefault<T> implements ValueIs<T> {

        @Override
        public T get(Setting<T> setting) {
            return setting.defaultValue.get();// could depends on situation
        }
    }

    public static class ValueIsCustom<T> implements ValueIs<T> {
        // for debug / or maybe to display also save source

        public ValueIsCustom(T is) {
            custom = is;
        }

        T custom;

        @Override
        public T get(Setting<T> setting) {
            return custom;
        }
    }// could also have ValueIsTemporary and ValueIsFromOtherUser...


    public static class VizSettings<T> { // this might not make sense for some types but it makes it easier to have for all...
        public T min;
        public T max;

        public boolean logScale; //  or more?

    }

}
