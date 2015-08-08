See http://www.liferay.com/community/wiki/-/wiki/Main/Resources+Importer+Web
for more information about the Resources Importer.



old resource importlet data

{
	"layoutTemplateId": "homelayout",
	"publicPages": [
		{
			"columns": [
				[
					{
                        "portletId": "56",
                        "portletPreferences": {
                            "articleId": "MAIN_CAROUSEL",
                            "groupId": "${groupId}",
                            "portletSetupCss": {
                            	"advancedData": {
                            		"customCSSClassName": "main-carousel"
                            	}
                            }
                        }
                    }
				],
				[
					{
                        "portletId": "56",
                        "portletPreferences": {
                            "articleId": "ABOUT_US",
                            "groupId": "${groupId}",
                        }
                    }
				],
				[
                    {
                        "portletId": "1_WAR_webformportlet_INSTANCE_${groupId}",
                        "portletPreferences": {
                        	"title" : "Quick Contact",
							"description" : "",
							"fieldLabel1": "Name",
							"fieldType1": "text",
							"fieldLabel2": "Email",
							"fieldType2": "text",
							"fieldLabel3": "Phone",
							"fieldType3": "text",
							"fieldLabel4":"Message",
							"fieldType4": "text",
							"portletSetupCss": {
								"advancedData": {
									"customCSSClassName": "quick-contact"
								}
							}
						}
                    }
                ],
				[
					{
                        "portletId": "56",
                        "portletPreferences": {
                            "articleId": "SHIPPING_DETAILS",
                            "groupId": "${groupId}",
                        }
                    }
				]
			],
			"friendlyURL": "/home",
			"name": "Home",
			"title": "Home"
		},
		{
			"title": "Pet Foods",
			"name": "Pet Foods",
			"friendlyURL": "/pet-food",
			"layouts": [
                {
                    "friendlyURL": "/dfa",
                    "name": "Dog Food & Accessories",
                    "title": "Dog Food & Accessories"
                },
                {
                    "friendlyURL": "/cfa",
                    "name": "Cat Food & Accessories",
                    "title": "Cat Food & Accessories"
                },
                {
                    "friendlyURL": "/bfa",
                    "name": "Bird Food & Accessories",
                    "title": "Bird Food & Accessories"
                },
                {
                    "friendlyURL": "/sf",
                    "name": "Small Animal Food & Accessories",
                    "title": "Small Animal Food & Accessories"
                }
            ]
		},
		{
			"title": "Exotic Pet Birds",
			"name": "Exotic Pet Birds",
			"friendlyURL": "/exotic-pet-birds",
			"layouts": [
                {
                    "friendlyURL": "/parakeets",
                    "name": "Parakeets",
                    "title": "Parakeets"
                },
                {
                    "friendlyURL": "/macaws",
                    "name": "Macaws",
                    "title": "Macaws"
                },
                {
                    "friendlyURL": "/cockatoos",
                    "name": "Cockatoos",
                    "title": "Cockatoos"
                },
                {
                    "friendlyURL": "/lorikeets",
                    "name": "Lorikeets",
                    "title": "Lorikeets"
                },
                {
                    "friendlyURL": "/austarlian-parakeets",
                    "name": "Austarlian Parakeets",
                    "title": "Austarlian Parakeets"
                },
                {
                    "friendlyURL": "/amazons",
                    "name": "Amazons",
                    "title": "Amazons"
                }
                
            ]
		},
		{
			"title": "Exotic Pet Animals",
			"name": "Exotic Pet Animals",
			"friendlyURL": "/exotic-pet-animals",
			"layouts": [
                {
                    "friendlyURL": "/bull-dog",
                    "name": "Bull Dog",
                    "title": "Bull Dog"
                }
            ]
		},
		{
			"title": "Accessories",
			"name": "Accessories",
			"friendlyURL": "/accessories"
		},
		{
			"title": "Feed",
			"name": "Feed",
			"friendlyURL": "/feed"
		},
		{
			"title": "Fish",
			"name": "Fish",
			"friendlyURL": "/fish"
		},
		{
			"title": "About Us",
			"name": "About Us",
			"friendlyURL": "/about-us"
		},
		{
			"title": "Contact Us",
			"name": "Contact Us",
			"friendlyURL": "/contact-us"
		}
	]
}