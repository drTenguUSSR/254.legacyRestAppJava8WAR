# http2dto-info

## 🔄 Цепочка преобразований

Процесс обработки запроса выглядит так:

1. **HTTP Transport Layer**: Принимает HTTP-запрос, определяет `Content-Type`
как `application/json` и находит подходящий `MessageBodyReader`.
2. **Jersey JSON Provider (`JSONRootElementProvider`)**: Используется
провайдер `com.sun.jersey.json.impl.provider.entity.JSONRootElementProvider.App`
, который является частью модуля `jersey-json`. Этот провайдер отвечает
за чтение JSON из входного потока и его преобразование, используя
кастомный JAXB-контекст, предоставленный `JAXBContentResolver`.
*дополнение*: `Jersey JSON Provider`:
`com.sun.jersey.json.impl.provider.entity.JSONRootElementProvider`
`jersey-json` работает в режиме JAXB-Based Mapping т.к. POJOMappingFeature=false
3. **Кастомный JAXBContentResolver**: Через механизм `ContextResolver`
Jersey получает `JAXBContext`, явно созданный в вашем классе
`JAXBContentResolver`. Этот контекст использует реализацию
`com.sun.xml.bind.v2.runtime.JAXBContextImpl` (внешняя реализация
JAXB, а не внутренняя из JDK). Это позволяет использовать единый
контекст для всех DTO-классов, обнаруженных в заданных пакетах.
4. **JAXB Unmarshaller**: Внутри `JAXBContentResolver` используется внешняя
реализация JAXB (`com.sun.xml.bind.v2.runtime.JAXBContextImpl`). На этом этапе
аннотации JAXB в DTO-классах интерпретируются для управления процессом десериализации.
5. **JAXBElement<CommonRequestDto>**: Результат работы JAXB – объект-обёртка.
6. **CommonRequestDto**: Конечный DTO-объект, который получает ваш контроллер.

## 💡 Практическое значение для диагностики ошибки 400

Проблема, скорее всего, возникает на стыке **слоёв 3 и 4**, когда
`JSONRootElementProvider` взаимодействует с JAXB-контекстом.

## Как JAXB выбирает реализацию

JAXB использует несколько шагов для поиска фабрики. Вот полный порядок:

### 🎯 Высший приоритет (JAX-RS уровень)

#### 1. **JAX-RS ContextResolver<JAXBContext>**

```java
@Provider
public class JAXBContentResolver implements ContextResolver<JAXBContext> {
    @Override
    public JAXBContext getContext(Class<?> type) {
        // Явное создание контекста с полным контролем
        return JAXBContext.newInstance(classes);
    }
}
```

**Приоритет**: Наивысший в JAX-RS контексте  
**Когда используется**: Когда Jersey нужен JAXBContext для конкретного типа  
**Преимущество**: Полный программный контроль над созданием контекста

### 🔧 Базовый уровень (JAXB API)

#### 2. **Файл jaxb.properties в пакете DTO**

```properties
javax.xml.bind.context.factory=com.sun.xml.bind.v2.runtime.JAXBContextImpl
```

Поиск файла jaxb.properties: JAXB ищет файл jaxb.properties в пакетах ваших
классов DTO. Если файл найден и содержит свойство `javax.xml.bind.context.factory`
, будет использовано указанное в нем значение.
*пример*: непровено, MOXy вариант:
`org.eclipse.persistence:org.eclipse.persistence.moxy:2.7.12`

```properties
javax.xml.bind.context.factory=org.eclipse.persistence.jaxb.JAXBContextFactory
```

**Приоритет**: Высокий (пакет-специфичный)  
**Расположение**: `src/main/resources/mil/teng254/legacy/dto/jaxb.properties`  
**Особенность**: Применяется только к классам в этом пакете

#### 3. **Системное свойство**

Проверка системного свойства: Проверяется глобальное системное свойство
`javax.xml.bind.JAXBContextFactory`.

```java
System.setProperty("javax.xml.bind.JAXBContextFactory",  
"com.sun.xml.bind.v2.runtime.JAXBContextImpl");
```

**Приоритет**: Средний (глобальный)  
**Способ установки**: VM параметр или код инициализации  
**Охват**: Все приложение

#### 4. **ServiceLoader механизм**

Поиск через ServiceLoader: JAXB ищет файл
`META-INF/services/javax.xml.bind.JAXBContext` в classpath.

**Приоритет**: Низкий  
**Как работает**: JAXB ищет файлы конфигурации в JAR-файлах  
**Редкость**: Малоиспользуемый на практике

### ⚠️ Резервные варианты

#### 5. **Реализация по умолчанию из JDK**

Использование реализации по умолчанию: Если другие способы не сработали,
используется реализация по умолчанию. В вашем случае, судя по предыдущим
проверкам, это `com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl` из JDK

```java
com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl
```

**Приоритет**: Самый низкий  
**Когда используется**: Если другие механизмы не сработали  
**Ограничение**: Не поддерживает JSON нативно

#### 6. **Jersey JSON Provider**

```java
com.sun.jersey.json.impl.provider.entity.JSONRootElementProvider.App
```

**Особый случай**: Не выбор JAXB реализации, а обертка Jersey  
**Функция**: Преобразует JSON ↔ JAXB объекты используя стандартный JAXBContext

## 🎪 Текущая конфигурация проекта

В вашем проекте работает **комбинированный подход**:

1. **JAXBContentResolver** ← Активный (через сканирование пакетов)
2. **Стандартный JAXB из JDK** ← Резервный (com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl)
3. **Jersey JSON Provider** ← Адаптер для JSON обработки

Этот иерархический подход обеспечивает максимальную
гибкость и контроль над обработкой JAXB в рамках JAX-RS
приложения.
