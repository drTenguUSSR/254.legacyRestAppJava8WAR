### 🔄 Цепочка преобразований

Процесс обработки запроса выглядит так:

1.  **HTTP Transport Layer**: Поступление сырого HTTP-запроса с телом в 
 формате JSON.
2.  **Jersey Container**: Контейнер Jersey получает запрос, идентифицирует
 MediaType как `application/json` и ищет подходящий `MessageBodyReader`.
3.  **Jersey JSON Provider (`JSONRootElementProvider`)**: Найденный
провайдер `JSONRootElementProvider` — это ключевое звено. Он отвечает 
за чтение JSON из входного потока и его преобразование. 
Этот провайдер является частью модуля `jersey-json` и предназначен 
для работы с JAXB-аннотированными классами, как `CommonRequestDto`.
`Jersey JSON Provider`: 
`com.sun.jersey.json.impl.provider.entity.JSONRootElementProvider`
`jersey-json` работает в режиме JAXB-Based Mapping т.к. POJOMappingFeature=false
4.  **JAXB Unmarshaller**: Внутри `JSONRootElementProvider` используется
стандартная реализация JAXB из JDK 
(`com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl`). 
Именно на этом этапе аннотации JAXB в `CommonRequestDto` 
(такие как `@XmlElement(required = false, nillable = true)`) 
интерпретируются для управления процессом десериализации.

&#x2623;&#xFE0F;нужно ------------------ переделать на `com.sun.xml.bind.v2.runtime.JAXBContextImpl`&#x2623;&#xFE0F;

5.  **JAXBElement<CommonRequestDto>**: Результат работы 
JAXB — это объект-обёртка.
6.  **CommonRequestDto**: Ваш контроллер получает итоговый 
DTO-объект после извлечения значения из `JAXBElement`.

### 💡 Практическое значение для диагностики

Это знание позволяет сузить круг поиска причины ошибки 400. Проблема,
скорее всего, возникает на стыке **слоёв 3 и 4**, когда
`JSONRootElementProvider` взаимодействует с JAXB-контекстом.

Для дальнейшей диагностики вы можете попробовать следующие шаги:

*   **Включите детальное логирование Jersey**: Попробуйте найти в
настройках Jersey или Log4j возможность включить логирование на
уровне `DEBUG` или `TRACE` для пакета `com.sun.jersey`. Это может
показать сырой запрос и более детальные сообщения об ошибках парсинга.
*   **Сфокусируйтесь на JAXB-аннотациях**: Поскольку провайдер 
напрямую опирается на JAXB, повторно проверьте корректность
аннотаций в `CommonRequestDto` и их совместимость с обработкой JSON.

### Как JAXB выбирает реализацию

JAXB использует несколько шагов для поиска фабрики. Вот полный порядок:

- Поиск файла jaxb.properties: JAXB ищет файл jaxb.properties в пакетах ваших
классов DTO. Если файл найден и содержит свойство `javax.xml.bind.context.factory`
, будет использовано указанное в нем значение.

- Проверка системного свойства: Проверяется глобальное системное свойство
`javax.xml.bind.JAXBContextFactory`.

- Поиск через ServiceLoader: JAXB ищет файл 
`META-INF/services/javax.xml.bind.JAXBContext` в classpath.

- Использование реализации по умолчанию: Если другие способы не сработали,
используется реализация по умолчанию. В вашем случае, судя по предыдущим
проверкам, это `com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl` из JDK
