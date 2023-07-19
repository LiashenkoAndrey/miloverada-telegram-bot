package gov.miloverada.telegram_bot.impl;

import gov.miloverada.telegram_bot.controllers.exceptions.*;
import gov.miloverada.telegram_bot.interfaces.MethodExecutor;
import gov.miloverada.telegram_bot.util.annotations.CallBack;
import gov.miloverada.telegram_bot.util.annotations.Command;
import gov.miloverada.telegram_bot.util.annotations.Controller;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MethodExecutorImpl implements MethodExecutor {

    private static final Logger logger = LogManager.getLogger(MethodExecutorImpl.class);

    private Map<String, Method> callBacks;
    private Map<String, Method> commands;

    private Map<String, Method> methods;

    private final ApplicationContext context;

    @PostConstruct
    private void prepare() {
        List<Method> methods = findAllMethods();
        this.methods = methods.stream()
                .collect(Collectors.toMap(Method::getName, Function.identity()));
        this.callBacks = findCallbacks(methods);
        this.commands = findCommands(methods);
    }

    @Override
    public void invokeCallBack(Update update) throws CallBackMethodExecutionException {
        try {
            CallbackQuery query = update.getCallbackQuery();
            String data = query.getData();
            if (hasParams(data)) {
                invokeCallBackWithParams(data);
            } else {
                invokeCallBack(data);
            }
        } catch (MethodExecutionException e) {
            logger.error(e.getMessage());
            throw new CallBackMethodExecutionException(e);
        }
    }


    @Override
    public void invokeCommand(Update update) throws CommandControllerException {
        try {
            String commandName = update.getMessage().getText().replace("/", "");

            if (commands.containsKey(commandName)) {
                Method method = commands.get(commandName);

                invokeMethod(method);
            } else {
                throw new MethodIsNotPresentException("Method with name '" + commandName + "' is not present");
            }

        } catch (MethodExecutionException e) {
            logger.error(e.getMessage());
            throw new CommandMethodExecutionException(e);
        }
    }

    private void invokeMethod(Method method) throws MethodExecutionException {
        try {
            if (method == null) throw new IllegalArgumentException("method is null");
            String className = parseClassNameFromClassToString(method);
            Object methodOwner = context.getBean(uncapitalize(className));

            method.invoke(methodOwner);
        } catch (BeansException | InvocationTargetException | IllegalAccessException | MethodIsNotPresentException  e) {
            logger.error(e.getMessage());
            throw new MethodExecutionException(e);
        }
    }

    @Override
    public void invokeUtilMethod(String methodName) throws UtilMethodExecutionException {
        try {
            if (methods.containsKey(methodName)) {
                invokeMethod(methods.get(methodName));

            } else throw new MethodIsNotPresentException("Method with name '" + methodName + "' is not exist");
        } catch (MethodExecutionException e) {
            logger.error(e.getMessage());
            throw new UtilMethodExecutionException(e);
        }
    }

    /**
     * Changes a first char of a string to lover case
     * @param str string
     * @return changed string
     */
    private String uncapitalize(String str) {
        String firstChar = String.valueOf(str.charAt(0));
        return str.replaceFirst(firstChar, firstChar.toLowerCase());
    }



    /**
     * Invokes callback method
     * @param methodName methodName
     */
    public void invokeCallBack(String methodName) throws CallBackMethodExecutionException {
        try {
            Method method = callBacks.get(methodName);
            invokeMethod(method);

        }  catch (MethodExecutionException e) {
            logger.error(e.getMessage());
            throw new CallBackMethodExecutionException(e);
        }
    }


    /**
     * Parses callback data into method name and params.
     * Callback data has the next format: 'methodName?param1_param2_param3'
     * @param data callback data
     */
    public void invokeCallBackWithParams(String data) throws CallBackMethodExecutionException {
        try {
            String[] splittedData = data.split("\\?");
            String methodName = splittedData[0];

            Method method = callBacks.get(methodName);
            if (method != null) {
                String className = uncapitalize(parseClassNameFromClassToString(method));
                Object methodOwner = context.getBean(className);

                Object[] params = splittedData[1].split("_");
                method.invoke(methodOwner, params);
            }

        }  catch (BeansException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e.toString());
            throw new CallBackMethodExecutionException(e);
        }
    }

    public String parseClassNameFromClassToString(Method method) {
        String className = method.getDeclaringClass().getName();
        String[] arr = className.split("\\.");
        return arr[arr.length-1];
    }


    public Map<String, Method> findCallbacks(List<Method> methods) {

        return methods.stream()
                .filter((method -> method.getAnnotation(CallBack.class) != null))
                .collect(Collectors.toMap(method -> {
                    String val = method.getAnnotation(CallBack.class).value();
                    return val.equals("") ? method.getName() : val;
                }, Function.identity()));
    }

    public Map<String, Method> findCommands(List<Method> methods) {

        return methods.stream()
                .filter((method -> method.getAnnotation(Command.class) != null))
                .collect(Collectors.toMap(method -> {
                    String val = method.getAnnotation(Command.class).value();
                    return val.equals("") ? method.getName() : val;
                }, Function.identity()));
    }


    public List<Method> findAllMethods() {
        Map<String, Object> controllers = context.getBeansWithAnnotation(Controller.class);
        List<Method> list = new ArrayList<>();

        controllers.values().forEach(
                (obj) -> list.addAll(Arrays.stream(obj.getClass().getDeclaredMethods()).toList())
        );

        return list;
    }


    public boolean hasParams(String data) {
        return data.contains("?");
    }

}
