package com.example.data.model

object PinTemplatesData {
    val templates: List<PinTemplate> = listOf(
        PinTemplate(
            id = "curiosity-story",
            name = "Curiosity Story",
            description = "Hooks readers with suspense, mystery, and an irresistible emotional gap that begs to be resolved.",
            recommendedTone = "Curious",
            ctaSuggestion = "Read More",
            exampleStructure = "Intriguing Hook → Unexpected Discovery → Emotional Pivot → Call to Read Full Story",
            defaultPinType = "Story",
            promptIdea = "A mysterious story about an unexpected discovery in an old home",
            iconCategory = "auto_stories"
        ),
        PinTemplate(
            id = "how-to-guide",
            name = "How-To Guide",
            description = "Direct, actionable step-by-step guidance that establishes authority and saves the pinner time.",
            recommendedTone = "Click-worthy",
            ctaSuggestion = "Learn More",
            exampleStructure = "End Goal / Benefit → 3-5 Actionable Steps → Pro Tip / Cheat Sheet → Save for Later",
            defaultPinType = "Tutorial",
            promptIdea = "How to propagate indoor fiddle-leaf fig plants for beginners",
            iconCategory = "build"
        ),
        PinTemplate(
            id = "listicle",
            name = "Listicle",
            description = "High-CTR numbered roundups, recommendations, and categorized resource collections.",
            recommendedTone = "Viral-style",
            ctaSuggestion = "See How",
            exampleStructure = "Compelling Number + Topic → High-Impact Highlights → Action Item → Bookmark / Save",
            defaultPinType = "List",
            promptIdea = "12 genius small kitchen storage hacks you wish you knew sooner",
            iconCategory = "format_list_numbered"
        ),
        PinTemplate(
            id = "blog-traffic",
            name = "Blog Traffic",
            description = "SEO-optimized evergreen pins built to drive consistent referral clicks to articles and blog posts.",
            recommendedTone = "Professional",
            ctaSuggestion = "Read More",
            exampleStructure = "Core Search Intent Keyword → Value Promise → Article Teaser → Click Through",
            defaultPinType = "Blog Post",
            promptIdea = "Complete beginner guide to high-yield savings accounts and budgeting in 2026",
            iconCategory = "language"
        ),
        PinTemplate(
            id = "product-spotlight",
            name = "Product Spotlight",
            description = "Conversion-focused pin showcasing product benefits, aesthetic appeal, and unique selling points.",
            recommendedTone = "Luxury",
            ctaSuggestion = "Shop Now",
            exampleStructure = "Hero Product Aesthetic → Key Problem Solved → Unique Feature → Direct Shop Link",
            defaultPinType = "Product",
            promptIdea = "Handcrafted organic ceramic matcha bowl set with bamboo whisk",
            iconCategory = "shopping_bag"
        ),
        PinTemplate(
            id = "inspirational",
            name = "Inspirational",
            description = "Uplifting, aesthetically pleasing quotes, affirmations, and lifestyle aspirations.",
            recommendedTone = "Inspirational",
            ctaSuggestion = "Discover More",
            exampleStructure = "Memorable Insight or Quote → Emotional Resonance → Reflection Hook → Pin to Vision Board",
            defaultPinType = "Quote",
            promptIdea = "Mindful morning affirmations for calm focus and anxiety reduction",
            iconCategory = "format_quote"
        ),
        PinTemplate(
            id = "tutorial",
            name = "Tutorial",
            description = "Practical skill-building pins focusing on recipes, crafts, coding, or DIY transformations.",
            recommendedTone = "Friendly",
            ctaSuggestion = "Get the Recipe",
            exampleStructure = "Finished Result Showcase → Key Ingredients / Tools → Quick Overview → Full Recipe / Guide",
            defaultPinType = "Recipe",
            promptIdea = "20-minute creamy garlic Tuscan chicken skillet with sundried tomatoes",
            iconCategory = "restaurant"
        ),
        PinTemplate(
            id = "before-after",
            name = "Before & After",
            description = "Visual contrast demonstrating transformation, room makeovers, fitness results, or organization.",
            recommendedTone = "Click-worthy",
            ctaSuggestion = "See How",
            exampleStructure = "Initial State Problem → Transformation Reveal → Timeline/Budget Callout → View Breakdown",
            defaultPinType = "Idea",
            promptIdea = "Tiny walk-in closet makeover under $150 with IKEA shelving hacks",
            iconCategory = "compare"
        ),
        PinTemplate(
            id = "problem-solution",
            name = "Problem → Solution",
            description = "Directly validates an annoying pain point and presents your content as the ultimate remedy.",
            recommendedTone = "Professional",
            ctaSuggestion = "Learn More",
            exampleStructure = "Frustrating Problem Callout → Why Common Advice Fails → The Real Fix → Read Guide",
            defaultPinType = "Blog Post",
            promptIdea = "Why your sourdough bread won't rise (and the simple humidity fix)",
            iconCategory = "help_outline"
        ),
        PinTemplate(
            id = "viral-hook",
            name = "Viral Hook",
            description = "High engagement format using contrarian questions, surprising facts, or trend commentary.",
            recommendedTone = "Viral-style",
            ctaSuggestion = "Discover More",
            exampleStructure = "Bold Contrarian Statement → Curiosity Stacker → Evidence / Teaser → Discuss in Comments",
            defaultPinType = "Idea",
            promptIdea = "The 5 daily habits productive people silently stopped doing this year",
            iconCategory = "trending_up"
        )
    )
}
