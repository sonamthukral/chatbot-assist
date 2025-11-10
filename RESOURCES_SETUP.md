# Resources Setup

The crisis resources JSON data has been provided in your message. To complete the setup:

1. Copy the JSON array from your message (starting with `[` and containing all resource objects)
2. Save it to: `src/main/resources/resources_full.json`

The JSON structure should be an array of resource objects matching the `Resource` class structure.

## Resource Structure

Each resource object should have:
- `name`: String
- `parent_agency`: String
- `categories`: Array of strings
- `description`: String
- `location`: Object with address fields
- `mailing_address`: Object with address fields
- `hours`: String
- `phones`: Object with phone number fields
- `contact`: Object with email and website
- `service_area`: Object with areas_covered and coverage_by_county arrays
- `eligibility`: Object with general eligibility and demographic flags
- `fees`: String
- `application_process`: String
- `requirements`: Object with service and documents fields
- `languages_offered`: Array of strings

## Usage

Once the JSON file is in place, you can use the ResourceManager:

```java
ResourceManager manager = new ResourceManager("resources_full.json");
List<Resource> allResources = manager.getAllResources();
List<Resource> mentalHealth = manager.getResourcesByCategory("Mental Health Services");
List<Resource> davidson = manager.getResourcesByCounty("TN - Davidson County");
```

